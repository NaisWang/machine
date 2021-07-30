package com.example.server.controller;


import com.example.server.service.impl.RoleServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@RestController
@RequestMapping("/role")
public class RoleController {
	@Autowired
	private RoleServiceImpl roleService;

	@ApiModelProperty("获取所有角色")
	@GetMapping("/")
	public RespBean getRole() {
		return RespBean.success("获取成功", roleService.list(null));
	}

}
