package com.example.server.controller;

import com.example.server.service.impl.EmployeeServiceImpl;
import com.example.server.service.impl.ImageServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : whz
 */
@RestController
@RequestMapping("/employee/corr")
public class EmpCorrController {

	@Autowired
	private ImageServiceImpl imageService;
	@Autowired
	private EmployeeServiceImpl employeeService;

	@ApiOperation("获取所有用户图像")
	@GetMapping("/image")
	public RespBean getImageCorr() {
		return RespBean.success("操作成功", imageService.list(null));
	}

	@ApiOperation("查看员工CORR")
	@GetMapping("/name")
	public RespBean getEmpCorr() {
		return RespBean.success("操作成功", employeeService.getEmpCorr());
	}

}
