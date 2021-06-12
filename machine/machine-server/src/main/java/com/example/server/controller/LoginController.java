package com.example.server.controller;

import com.example.server.pojo.LoginParam;
import com.example.server.service.LoginService;
import com.example.server.service.impl.LoginServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : whz
 */
@RestController
public class LoginController {

	@Autowired
	LoginServiceImpl loginService;

	@ApiOperation(value = "登陆之后返回token")
	@PostMapping("/login")
	public RespBean login(@RequestBody LoginParam loginParam) {
		return loginService.login(loginParam);
	}
}
