package com.db.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.db.common.vo.Node;
import com.db.sys.entity.SysMenu;
/**
 * 菜单数据层对象
 * @author Tarena
 *
 */
public interface SysMenuDao {
	
	List<Map<String,Object>> findObjects();
	/**
	 * 统计菜单是否有子菜单
	 * @param id
	 * @return 子菜单的个数
	 */
	int getChildCount(Integer id);
	/**
	 * 删除菜单元素
	 * @param id
	 * @return
	 */
	int deleteObject(Integer id);
	/**
	 * 查询菜单的节点信息
	 * @return
	 */
	List<Node> findZtreeMenuNodes();
	
	/**
	 * 将内存中的menu对象持久化到数据库
	 * @param menu 菜单对象,其内容最初来自页面
	 * @return 插入的行数
	 */
	int insertObject(SysMenu menu);
	
	/**
	 * 将内存中的menu对象更新到数据库
	 * @param menu 菜单对象,其内容最初来自页面
	 * @return 修改的行数
	 */
	int updateObject(SysMenu menu);
	
	List<String> findPermissions(
			@Param("menuIds")Integer[] menuIds);
}
