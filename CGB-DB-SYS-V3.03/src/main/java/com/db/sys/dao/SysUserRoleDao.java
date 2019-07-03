package com.db.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.db.sys.entity.SysUser;

/**
 * 借助此dao操作用户和角色关系表
 * @author Tarena
 *
 */
public interface SysUserRoleDao {
	/**
	 * 基于角色id删除角色和用户关系数据
	 * @param roleId
	 * @return
	 */
	int deleteObjectsByRoleId(@Param("roleId")Integer roleId);
	/**
	 * 将用户角色关系数据写入用户角色关系表
	 * @param userId
	 * @param roleIds
	 * @return
	 */
	int insertObjects(
			@Param("userId")Integer userId,
			@Param("roleIds")Integer[] roleIds);
	/**
	 * 基于用户id找到角色id
	 * @param id
	 * @return
	 */
	List<Integer> findRoleIdsByUserId(Integer id);
	
	int updateObject(SysUser entity);
	
	int deleteObjectsByUserId(Integer userId);
}
