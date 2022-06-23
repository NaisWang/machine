package com.example.server.controller;


import com.example.server.pojo.Employee;
import com.example.server.pojo.Menu;
import com.example.server.pojo.Role;
import com.example.server.service.impl.MenuServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@RestController
@RequestMapping("/home/menu")
public class MenuController {

	@Autowired
	private MenuServiceImpl menuService;

	@ApiOperation("获取所有菜单，包括子菜单")
	@GetMapping("/")
	public RespBean getAllMenuWithChildren(Authentication authentication) {
		List<Role> roleList = ((Employee) authentication.getPrincipal()).getRoles();
		StringBuffer sb = new StringBuffer();
		for (Role role : roleList) {
			sb.append(role.getId() + ",");
		}
		List<Menu> menuList = menuService.getAllMenuWithChildren(sb.subSequence(0, sb.length() - 1).toString());
		return RespBean.success("操作成功", menuList);
	}

}
