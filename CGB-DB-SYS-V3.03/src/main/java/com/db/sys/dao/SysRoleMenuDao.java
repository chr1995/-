package com.db.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 基于此接口操作角色/菜单关系表(sys_role_menu)
 * @author Tarena
 *
 */
public interface SysRoleMenuDao {
	/**
	 * 基于菜单id删除关系表数据
	 * @param menuId 菜单id
	 * @return 删除的行数
	 */
	int deleteObjectsByMenuId(Integer menuId);
	/**
	 * 基于角色id删除关系数据表数据
	 * @param roleId
	 * @return
	 */
	int deleteObjectsByRoleId(Integer roleId);
	
	
	int insertObjects(
			@Param("roleId")Integer roleId,
			@Param("menuIds")Integer[] menuIds);
	
	
	List<Integer> findMenuIdsByRoleIds(
			@Param("roleIds")Integer[] roleIds);
}
