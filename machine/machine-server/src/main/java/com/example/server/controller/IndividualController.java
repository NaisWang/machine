package com.example.server.controller;

import com.example.server.pojo.Employee;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.ReflectPermission;

/**
 * @author : whz
 */
@RestController
@RequestMapping("/individual/all")
public class IndividualController {

	@ApiOperation("获取个人信息")
	@GetMapping("/")
	public RespBean getInfo(Authentication authentication) {
		Employee employee = ((Employee) authentication.getPrincipal());
		employee.setPassword(null);
		return RespBean.success("操作成功", employee);
	}
}
