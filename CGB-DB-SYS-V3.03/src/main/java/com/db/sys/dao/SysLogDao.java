package com.db.sys.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.db.sys.entity.SysLog;

/**
 *定义用户行为日志接口 
 *
 * spring-respository.xml 此
 * 文件中定义了一个MapperScannerConfigurer
 * 对象,此对象会对BasePackage属性指定的包
 * 下接口进行扫描,然后会为接口创建实现类的对象,
 * 并将这个对象存储到bean池,其key为接口名(首字母小写).
 */
public interface SysLogDao {
	/**
	 * 基于条件查询当前页要呈现的日志信息
	 * @param username 用户名
	 * @param startIndex 当前页起始位置
	 * @param pageSize 页面大小(每页最多显示多少)
	 * @return 当前页的日志记录信息
	 * 数据库中每条日志信息封装到一个SysLog对象中
	 */
	List<SysLog> findPageObjects(
			//用注解修饰参数会自动放入map中
			@Param("username")String username,
			@Param("startIndex")Integer startIndex,
			@Param("pageSize")Integer pageSize);
	/**
	 * 基于查询条件统计总记录数
	 * @param username 查询条件(例如查询哪个用户的日志信息)
	 * @return 总记录数(基于这个结果可以计算总页数)
	 * 说明：假如如下方法没有使用注解修饰，在基于名字进行查询
	 * 时候会出现There is no getter for property named
	 * 'username' in 'class java.lang.String'
	 */
	//参数用在动态SQL中,需要加注解                                      局部变量
	
	int getRowCount(@Param("username") String username);
		
	
	/**
	 *基于id删除日志信息 
	 */
	 int deleteObjects(
			 @Param("ids") Integer... ids);
	 
	 
	 int insertObject(SysLog entity);
	 
}
