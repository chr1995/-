package com.db.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.db.common.vo.CheckBox;
import com.db.sys.entity.SysRole;
import com.db.sys.vo.SysRoleMenuVo;

public interface SysRoleDao {
	/**
     * 分页查询角色信息
     * @param startIndex 上一页的结束位置
     * @param pageSize 每页要查询的记录数
     * @return
     */
	List<SysRole> findPageObjects(
             @Param("name")String name,
			@Param("startIndex")Integer startIndex,
			@Param("pageSize")Integer pageSize);
	/**
	 * 查询记录总数
	 * @return
	 */
	int getRowCount(@Param("name")String name);
	
	int deleteObject(Integer id);
	
	/**
	 * 将角色信息更新写入数据库
	 * @param entity
	 * @return
	 */
	int insertObject(SysRole entity);
	
	SysRoleMenuVo findObjectById(Integer id);
	
	int updateObject(SysRole entity);
	
	List<CheckBox> findObjects();
	/**
	 * 按名字统计角色相关信息
	 * @param name
	 * @return
	 */
	int getRowCountByName(String name);
}
