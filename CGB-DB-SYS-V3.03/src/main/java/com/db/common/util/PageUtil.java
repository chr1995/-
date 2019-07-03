package com.db.common.util;

import java.util.List;

import com.db.common.vo.PageObject;
import com.db.sys.entity.SysLog;
import com.db.sys.entity.SysRole;
/*分页工具类*/
public abstract class PageUtil {
	          //泛型方法,在方法的左侧加泛型
	public static <T>PageObject<T> newInstance(Integer pageCurrent, int rowCount, int pageSize, List<T> records){
		PageObject<T> pageObject = new PageObject<T>();
			pageObject.setPageCurrent(pageCurrent);
			pageObject.setPageSize(pageSize);
			pageObject.setRowCount(rowCount);
			pageObject.setRecords(records);
			pageObject.setPageCount((rowCount-1)/pageSize+1);
		return pageObject;
	}	
}
