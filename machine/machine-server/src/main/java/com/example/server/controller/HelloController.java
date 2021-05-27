package com.example.server.controller;

import com.example.server.utils.RespBean;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : whz
 */
@RestController
public class HelloController {

	@GetMapping("/")
	public String hello() {
		return "hello";
	}

}
