package com.db.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.db.sys.entity.SysUser;
import com.db.sys.vo.SysUserDeptVo;

public interface SysUserDao {
	
	int getRowCount(@Param("username")String username);
	/**
	 * 按条件查询当前页记录
	 * @param username
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	List<SysUserDeptVo> findPageObjects(
			@Param("username")String username,
			@Param("startIndex")Integer startIndex,
			@Param("pageSize")Integer pageSize);
	/**
	 * 禁用和启动
	 * @param id
	 * @param valid
	 * @param modifiedUser
	 * @return
	 */
	int validById(
			@Param("id")Integer id,
			@Param("valid")Integer valid,
			@Param("modifiedUser")String modifiedUser);
	/**
	 * 保存用户自身信息
	 * @param entity
	 * @return
	 */
	int insertObject(SysUser entity);
	
	SysUserDeptVo findObjectById(Integer id);
	
	int updateObject(SysUser entity);
	
	/**
	 * 基于用户名查询用户信息
	 */
	SysUser findUserByUserName(String username);
	
	int updatePassword(
			@Param("id")Integer id,
			@Param("newPwd")String newPwd,
			@Param("newSalt")String newSalt);
}
