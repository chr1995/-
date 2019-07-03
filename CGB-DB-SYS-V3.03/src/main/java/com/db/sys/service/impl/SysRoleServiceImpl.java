package com.db.sys.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.druid.util.StringUtils;
import com.db.common.exception.ServiceException;
import com.db.common.util.PageUtil;
import com.db.common.vo.CheckBox;
import com.db.common.vo.PageObject;
import com.db.sys.dao.SysRoleDao;
import com.db.sys.dao.SysRoleMenuDao;
import com.db.sys.dao.SysUserRoleDao;
import com.db.sys.entity.SysRole;
import com.db.sys.service.SysRoleService;
import com.db.sys.vo.SysRoleMenuVo;

@Service
public class SysRoleServiceImpl implements SysRoleService {
	@Autowired
	private SysRoleDao sysRoleDao;
	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	
	@Override
	public PageObject<SysRole> findPageObjects(String name, Integer pageCurrent) {
		//1.参数校验
		if(pageCurrent==null||pageCurrent<1)
			throw new IllegalArgumentException("当前页码不存在");
		//2.查询总记录数并进行验证
		int rowCount=sysRoleDao.getRowCount(name);
		if(rowCount==0)
			throw new ServiceException("没有记录");
		//3.查询当前页记录
		int pageSize=3;
		int startIndex=(pageCurrent-1)*pageSize;
		List<SysRole> records = sysRoleDao.findPageObjects(name, startIndex, pageSize);
		/*//4.对分页信息以及当前页记录进行封装
		  //4.1)构建PageObject对象
		  PageObject<SysRole> pageObject=new PageObject<>();
		  //4.2)封装数据
		  pageObject.setPageCurrent(pageCurrent);
		  pageObject.setPageSize(pageSize);
		  pageObject.setRowCount(rowCount);
		  pageObject.setRecords(records);
		  pageObject.setPageCount((rowCount-1)/pageSize+1);
		//5.返回封装结果。
		return pageObject;*/
		return PageUtil.newInstance(pageCurrent, rowCount, pageSize, records);
	}

	@Override
	public int deleteObject(Integer id) {
		//1.参数校验
		if(id==null||id<1)
			throw new IllegalArgumentException("id不能为空");
		//2.执行删除(防止强关联,先删除关系数据,再删除自身)
		//2.1删除角色和用户关系数据
		sysUserRoleDao.deleteObjectsByRoleId(id);
		//2.2删除角色和菜单关系数据
		
		sysRoleMenuDao.deleteObjectsByMenuId(id);
		//2.3删除角色自身信息
		int rows = sysRoleDao.deleteObject(id);
		if(rows==0)
			throw new ServiceException("记录可能已经不存在");
		//3.返回结果
		return rows;
	}

	@Override
	public int saveObject(SysRole entity, Integer[] menuIds) {
		//1.参数校验
		if(entity==null)
			throw new IllegalArgumentException("保存对象不存在");
		if(StringUtils.isEmpty(entity.getName()))
			throw new IllegalArgumentException("角色名不存在");
		if(menuIds==null||menuIds.length==0)
			throw new IllegalArgumentException("必须为角色赋予权限");
		//2.保存角色自身信息
		int rows = sysRoleDao.insertObject(entity);
		//3.保存角色和菜单的关系数据
		sysRoleMenuDao.insertObjects(entity.getId(), menuIds);
		return rows;
	}

	@Override
	public SysRoleMenuVo findObjectById(Integer id) {
		//1.参数校验
		if(id==null||id<1)
			throw new IllegalArgumentException("id参数无效");
		//2.基于id查询数据
		//2.1查询角色自身信息
		//2.2查询角色对应的菜单id
		SysRoleMenuVo roleMenuVo=
				sysRoleDao.findObjectById(id);
		if(roleMenuVo==null)
			throw new ServiceException("记录可能已经不存在了");
		return roleMenuVo;
	}

	@Override
	public int updateObject(SysRole entity,Integer[] menuIds) {
		//1.合法性验证
		if(entity==null)
			throw new ServiceException("更新的对象不能为空");
		if(entity.getId()==null)
			throw new ServiceException("id的值不能为空");

		if(StringUtils.isEmpty(entity.getName()))
			throw new ServiceException("角色名不能为空");
		if(menuIds==null||menuIds.length==0)
			throw new ServiceException("必须为角色指定一个权限");

		//2.更新数据
		int rows=sysRoleDao.updateObject(entity);
		if(rows==0)
			throw new ServiceException("对象可能已经不存在");
		sysRoleMenuDao.deleteObjectsByRoleId(entity.getId());
		sysRoleMenuDao.insertObjects(entity.getId(),menuIds);
		//3.返回结果
		return rows;
	}

	@Override
	public List<CheckBox> findObjects() {
		List<CheckBox> list=sysRoleDao.findObjects();
		if(list==null||list.isEmpty())
	        throw new ServiceException("系统没有角色信息");
		return list;
	}

}
