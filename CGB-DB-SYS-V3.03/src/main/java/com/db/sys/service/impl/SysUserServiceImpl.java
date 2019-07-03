package com.db.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.db.common.annotation.RequiredLog;
import com.db.common.exception.ServiceException;
import com.db.common.util.PageUtil;
import com.db.common.util.ShiroUtil;
import com.db.common.vo.PageObject;
import com.db.sys.dao.SysUserDao;
import com.db.sys.dao.SysUserRoleDao;
import com.db.sys.entity.SysLog;
import com.db.sys.entity.SysUser;
import com.db.sys.service.SysUserService;
import com.db.sys.vo.SysUserDeptVo;

@Service
@Transactional(
		rollbackFor=Throwable.class,
		isolation=Isolation.READ_COMMITTED,//只能读取别人已经提交的数据
		timeout=-1,readOnly=false)
public class SysUserServiceImpl implements SysUserService {
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	
	//SLF4J
	private Logger log=LoggerFactory.getLogger(SysUserServiceImpl.class);
	
	@Override
	public PageObject<SysUserDeptVo> findPageObjects(Integer pageCurrent, String username) {
		//1.参数校验
		if(pageCurrent==null||pageCurrent<1)
			throw new IllegalArgumentException("页码值不正确");
		//2.查询总记录数,并进行校验
		int rowCount =sysUserDao.getRowCount(username);
		if(rowCount==0)
			throw new ServiceException("没有对应记录");
		//3.查询当前页要呈现的记录
		int pageSize=3;
		int startIndex=(pageCurrent-1)*pageSize;
		List<SysUserDeptVo> records = sysUserDao.findPageObjects(username, startIndex, pageSize);
		return PageUtil.newInstance(pageCurrent, rowCount, pageSize, records);
	}
	
	@RequiredLog("禁用启用")
	@RequiresPermissions("sys:user:valid")
	@Override
	public int validById(Integer id, Integer valid, String modifiedUser) {
		//1.参数校验
		if(id==null||id<1)
			throw new IllegalArgumentException("id值无效");
		if(valid==null||(valid!=0&&valid!=1))
			throw new IllegalArgumentException("valid值无效");
		//2.修改状态
		int rows = sysUserDao.validById(id, valid, modifiedUser);
		if(rows==0)
			throw new ServiceException("记录可能已经不存在");
		//3.返回结果
		return rows;
	}
	@Override
	public int saveObject(SysUser entity, Integer[] roleIds) {
		//1.验证数据合法性
		if(entity==null)
			throw new IllegalArgumentException("保存对象不能为空");
		if(StringUtils.isEmpty(entity.getUsername())){
			
			log.debug("username is null");
			
			throw new IllegalArgumentException("用户名不能为空");
		}	
		if(StringUtils.isEmpty(entity.getPassword()))
			throw new IllegalArgumentException("密码不能为空");
		if(roleIds==null || roleIds.length==0)
			throw new ServiceException("至少要为用户分配角色");
		//2.将数据写入数据库(盐值加密)
		//2.1构建一个盐值对象
		String salt=UUID.randomUUID().toString();
		entity.setSalt(salt);
		//2.2对密码进行加密
		//加密(先了解,讲shiro时再说)
		SimpleHash sHash=
				new SimpleHash(//Shiro框架提供
						"MD5",//algorithmName 算法名称
						entity.getPassword(), //source要加密的对象
						salt,//盐值
						1024);//hashIterations 加密次数
		entity.setPassword(sHash.toHex());

		int rows=sysUserDao.insertObject(entity);
		sysUserRoleDao.insertObjects(
				entity.getId(),
				roleIds);//"1,2,3,4";
		//3.返回结果
		return rows;
	}
	@Transactional(readOnly=true,propagation=Propagation.REQUIRED)//允许并发读
	@Override
	public Map<String, Object> findObjectById(Integer userId) {
		//1.合法性验证
		if(userId==null||userId<=0)
			throw new ServiceException(
					"参数数据不合法,userId="+userId);
		//2.业务查询
		SysUserDeptVo user=
				sysUserDao.findObjectById(userId);
		if(user==null)
			throw new ServiceException("此用户已经不存在");
		List<Integer> roleIds=
				sysUserRoleDao.findRoleIdsByUserId(userId);
		//3.数据封装
		Map<String,Object> map=new HashMap<>();
		map.put("user", user);
		map.put("roleIds", roleIds);
		return map;
	}
	
	@Override
	public int updateObject(SysUser entity, Integer[] roleIds) {
		//1.参数有效性验证
		if(entity==null)
			throw new IllegalArgumentException("保存对象不能为空");
		if(StringUtils.isEmpty(entity.getUsername()))
			throw new IllegalArgumentException("用户名不能为空");
		if(roleIds==null||roleIds.length==0)
			throw new IllegalArgumentException("必须为其指定角色");
		//其它验证自己实现，例如用户名已经存在，密码长度，...
		//2.更新用户自身信息
		int rows=sysUserDao.updateObject(entity);
		//3.保存用户与角色关系数据
		sysUserRoleDao.deleteObjectsByUserId(entity.getId());
		sysUserRoleDao.insertObjects(entity.getId(),roleIds);
		
		//4.返回结果
		return rows;
	}

	@Override
	public int updatePassword(String password, String newPassword, String cfgPassword) {
		//1.参数验证
		//1.1原密码校验
		if(StringUtils.isEmpty(password))
		{
			System.out.println(password);
			throw new IllegalArgumentException("原密码不能为空");
		}
		SysUser user = ShiroUtil.getUser();
		SimpleHash sh = new SimpleHash("MD5",password,user.getSalt(),1);
		if(!sh.toHex().equals(user.getPassword()))
			throw new IllegalArgumentException("原密码不正确");
		//1.2新密码非空校验...
		if(StringUtils.isEmpty(newPassword))
			throw new IllegalArgumentException("新密码不能为空");
		if(!newPassword.equals(cfgPassword))
			throw new IllegalArgumentException("两次输入的密码不相等");
		//2.修改密码
		//2.1新密码加密
		//1.判定新密码与密码确认是否相同
		
		if(StringUtils.isEmpty(cfgPassword))
			throw new IllegalArgumentException("确认密码不能为空");
		
		
		//2.2新密码更新到数据库
		//sysUserDao.updatePassword(id, newPwd, newSalt);
		String newSalt=UUID.randomUUID().toString();
		String newPwd=
		    	new SimpleHash("MD5",newPassword, newSalt, 1).toHex();
		
		//4.将新密码加密以后的结果更新到数据库
		int rows=sysUserDao.updatePassword(user.getId(),newPwd, newSalt);
		if(rows==0)
			throw new ServiceException("修改失败");
		//3.返回结果
		return rows;
	}

}
