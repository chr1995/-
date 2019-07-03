package com.db.sys.service;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.db.common.vo.PageObject;
import com.db.sys.entity.SysUser;
import com.db.sys.vo.SysUserDeptVo;

@Service
public interface SysUserService {
	PageObject<SysUserDeptVo> findPageObjects(
			Integer pageCurrent,
			String username);
	
	int validById(
			@Param("id")Integer id,
			@Param("valid")Integer valid,
			@Param("modifiedUser")String modifiedUser);
	/**
	 * 保存用户以及用户对应的角色信息
	 * @param entity
	 * @param roleIds
	 * @return
	 */
	int saveObject(
			SysUser entity,
			Integer[] roleIds);
	
	Map<String,Object>  findObjectById(Integer userId);
	
	int updateObject(SysUser entity,Integer[] roleIds);
	/**
	 * 修改密码
	 * @param password
	 * @param newPassword
	 * @param cfgPassword
	 * @return
	 */
	int updatePassword(
			String password,
			String newPassword,
			String cfgPassword);
	
}
