package com.db.sys.service;

import java.util.List;

import com.db.common.vo.CheckBox;
import com.db.common.vo.PageObject;
import com.db.sys.entity.SysRole;
import com.db.sys.vo.SysRoleMenuVo;

public interface SysRoleService {
	/**
     * 本方法中要分页查询角色信息,并查询角色总记录数据
     * @param pageCurrent 当表要查询的当前页的页码值
     * @return 封装当前实体数据以及分页信息
     */
	 PageObject<SysRole> findPageObjects(
			 String name,Integer pageCurrent);
	 
	 /**
	  * 基于角色id删除角色数据
	  * @param id
	  * @return
	  */
	 int deleteObject(Integer id);
	 
	 int saveObject(SysRole entity,Integer[] menuIds);
	 /**
	  * 基于角色id获取角色以及对应的菜单信息
	  * @param id
	  * @return
	  */
	 SysRoleMenuVo findObjectById(Integer id);
	 
	 int updateObject(SysRole entity,Integer[] menuIds);
	 /**
	  * 查询所有角色信息
	  * @return
	  */
	 List<CheckBox> findObjects();
}
