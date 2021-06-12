package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.Employee;
import com.example.server.service.impl.EmployeeServiceImpl;
import com.example.server.utils.RespBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/personnel/emp")
public class EmployeeController {

	@Autowired
	private EmployeeServiceImpl employeeService;

	@ApiOperation("查看所有员工")
	@GetMapping("/")
	public RespBean getEmp() {
		List<Employee> employees = employeeService.getAllEmployee();
		return RespBean.success("请求成功", employees);
	}

	@ApiOperation("添加员工")
	@PostMapping("/")
	public RespBean addEmp(@RequestBody Employee employee) {
		if (employeeService.save(employee)) {
			return RespBean.success("添加成功");
		}
		return RespBean.error("添加失败");
	}

	@ApiOperation("删除员工")
	@DeleteMapping("/")
	public RespBean deleteEmpById(Integer id) {
		if (employeeService.removeById(id)) {
			return RespBean.success("删除成功");
		}
		return RespBean.error("删除失败");
	}

	@ApiOperation("修改员工")
	@PutMapping("/")
	public RespBean updateEmp(@RequestBody Employee employee) {
		if (employeeService.updateById(employee)) {
			return RespBean.success("修改成功");
		}
		return RespBean.error("删除失败");
	}

}
