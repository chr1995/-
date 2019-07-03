package com.db.sys.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.db.common.exception.ServiceException;
import com.db.common.util.PageUtil;
import com.db.common.vo.PageObject;
import com.db.sys.dao.SysLogDao;
import com.db.sys.entity.SysLog;
import com.db.sys.service.SysLogService;

@Service
public class SysLogServiceImpl implements SysLogService {
	@Autowired
	private SysLogDao sysLogDao;
	@Override
	public PageObject<SysLog> findPageObjects(String username,Integer pageCurrent) {
		//1.验证参数合法性(服务层校验,双重认证)
		//1.1验证pageCurrent的合法性，
		//不合法抛出IllegalArgumentException异常
		if(pageCurrent==null||pageCurrent<1)
			throw new IllegalArgumentException("当前页面不正确");
		//2.基于条件查询总记录数
		//2.1) 执行查询
		int rowCount=sysLogDao.getRowCount(username);
		//2.2) 验证查询结果，假如结果为0不再执行如下操作
		if(rowCount==0)
			throw new ServiceException("系统没有查到对应的记录");
		//3.基于条件查询当前页记录(pageSize定义为2)
		//3.1)定义pageSize
		int pageSize=3;
		//3.2)计算startIndex
		int startIndex=(pageCurrent-1)*pageSize;
		//3.3)基于起始位置,执行当前数据的查询操作
		List<SysLog> records = sysLogDao.findPageObjects(username, startIndex, pageSize);
		//4.对分页信息以及当前页记录进行封装
		//4.1)构建PageObject对象
		//4.2)封装数据
		//5.返回封装结果。
		return PageUtil.newInstance(pageCurrent, rowCount, pageSize, records);
	}

	@Override
	public int deleteObjects(Integer... ids) {
		//1.判定参数有效性
		if(ids==null||ids.length==0)
			throw new IllegalArgumentException("请先选择");
		//2.基于参数id执行删除操作
		int rows=0;
		try{
			rows=sysLogDao.deleteObjects(ids);
		}catch(Throwable e){
			e.printStackTrace();
			//报警,给运维人员发短信
			throw new ServiceException("系统维护中");
		}
		if(rows==0)
			throw new ServiceException("记录可能已经不存在了");
		//3.返回删除结果
		return rows;
	}

}
