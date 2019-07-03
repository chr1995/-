package com.db.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.db.common.vo.JsonResult;
import com.db.common.vo.PageObject;
import com.db.sys.entity.SysLog;
import com.db.sys.service.SysLogService;

@Controller//告诉spring,此类交给spring管理
@RequestMapping("/log/")//定义对外的url
public class SysLogController {
	//关联service层,解耦合,使其耦合于sevice接口
	@Autowired
	//@Qualifier("sysLogServiceImpl")//指定spring容器中以"sysLogServiceImpl"为key的注入
	private SysLogService sysLogService;
	
	@RequestMapping("doLogListUI")
	public String doLogListUI(){//use a string
		return "sys/log_list";
	}
	
	@GetMapping("doFindPageObjects")
	@ResponseBody
	public JsonResult doFindPageObjects(String username,Integer pageCurrent){
	 PageObject<SysLog> pageObject=
		sysLogService.findPageObjects(username,pageCurrent);
	return new JsonResult(pageObject);
	}
	
	@RequestMapping("doDeleteObjects")
	@ResponseBody
	public JsonResult doDeleteObjects(Integer... ids){
		sysLogService.deleteObjects(ids);
		return new JsonResult("delete ok");
	}
}
