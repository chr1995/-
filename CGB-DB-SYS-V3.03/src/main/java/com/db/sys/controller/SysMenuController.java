package com.db.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.db.common.vo.JsonResult;
import com.db.sys.entity.SysMenu;
import com.db.sys.service.SysMenuService;

@Controller
@RequestMapping("/menu/")
public class SysMenuController {
	@Autowired
	private SysMenuService sysMenuService;
	@RequestMapping("doMenuListUI")
	public String doMenuListUI(){
		return "sys/menu_list";
	}
	/**
	 * 查询所有菜单以及对应的上级菜单信息
	 * @return
	 */
	@RequestMapping("doFindObjects")
	@ResponseBody
	public JsonResult doFindObjects(){
		return new JsonResult(sysMenuService.findObjects());
	}
	
	@RequestMapping("doDeleteObject")
	@ResponseBody
	public JsonResult doDeleteObject(Integer id){
		sysMenuService.deleteObject(id);
		return new JsonResult("delete ok");
	}
	/**
	 * 返回菜单便捷页面
	 * @return
	 */
	@RequestMapping("doMenuEditUI")
	public String doMenuEditUI(){
		return "sys/menu_edit";
	}
	
	 @RequestMapping("doFindZtreeMenuNodes")
	 @ResponseBody
	 public JsonResult doFindZtreeMenuNodes(){
		 return new JsonResult(sysMenuService.findZtreeMenuNodes());
	 }
	 
	 @RequestMapping("doSaveObject")
	 @ResponseBody
	 public JsonResult doSaveObject(SysMenu menu){
		 sysMenuService.saveObject(menu);
		 return new JsonResult("save ok");
	 }
	 
	 @RequestMapping("doUpdateObject")
	 @ResponseBody
	 public JsonResult doUpdateObject(SysMenu menu){
		 sysMenuService.updateObject(menu);
		 return new JsonResult("update ok");
	 }
}
