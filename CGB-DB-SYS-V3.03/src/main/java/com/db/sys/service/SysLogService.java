package com.db.sys.service;

import com.db.common.vo.PageObject;
import com.db.sys.entity.SysLog;

/**
 * 日志模块业务接口:负责日志业务的规范定义 
 *
 */
public interface SysLogService {
	/**
	 * 按条件执行用户行为日志的查询操作
	 * @param pageCurrent 当前页码
	 * @param username 查询条件
	 * @return	当前需要的日志数据
	 */
	PageObject<SysLog> findPageObjects(String username,Integer pageCurrent);
	/**
	 * 基于id执行日志删除业务
	 * @param ids 对应多个id
	 * @return
	 */
	int deleteObjects(Integer... ids);
}
