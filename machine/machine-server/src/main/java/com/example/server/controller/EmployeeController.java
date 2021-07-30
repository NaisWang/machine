package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.Employee;
import com.example.server.pojo.EmployeeRole;
import com.example.server.service.impl.EmployeeRoleServiceImpl;
import com.example.server.service.impl.EmployeeServiceImpl;
import com.example.server.utils.RespBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Autowired
	private EmployeeRoleServiceImpl employeeRoleService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@ApiOperation("查看所有员工")
	@GetMapping("/")
	public RespBean getEmp(Employee employee) {
		System.out.println(employee);
		List<Employee> employees = employeeService.getAllEmployee(employee);
		return RespBean.success("请求成功", employees);
	}

	@ApiOperation("添加员工")
	@PostMapping("/")
	@Transactional
	public RespBean addEmp(@RequestBody Employee employee) {

		try {
			employee.setPassword(passwordEncoder.encode(employee.getPassword()));
			if (employeeService.save(employee)) {
				employeeRoleService.remove(new QueryWrapper<EmployeeRole>().eq("employee_id", employee.getId()));

				String[] ridStrs = employee.getAllRoleIds().split(",");
				List<Integer> rids = new ArrayList<>();
				for (String id : ridStrs) {
					rids.add(Integer.parseInt(id));
				}

				Integer result = employeeRoleService.addEmpRole(employee.getId(), rids.toArray(new Integer[0]));
				if (rids.size() == result) {
					return RespBean.success("添加成功");
				}
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改员工")
	@PutMapping("/")
	public RespBean updateEmp(@RequestBody Employee employee) {
		employee.setPassword(null);
		if (employeeService.updateById(employee)) {
			employeeRoleService.remove(new QueryWrapper<EmployeeRole>().eq("employee_id", employee.getId()));

			String[] ridStrs = employee.getAllRoleIds().split(",");
			List<Integer> rids = new ArrayList<>();
			for (String id : ridStrs) {
				rids.add(Integer.parseInt(id));
			}

			Integer result = employeeRoleService.addEmpRole(employee.getId(), rids.toArray(new Integer[0]));
			if (rids.size() == result) {
				return RespBean.success("添加成功");
			}
			return RespBean.success("修改成功");
		}
		return RespBean.error("删除失败");
	}

	//@ApiOperation("删除员工")
	//@DeleteMapping("/")
	//public RespBean deleteEmpById(Integer id) {
	//	if (employeeService.removeById(id)) {
	//		return RespBean.success("删除成功");
	//	}
	//	return RespBean.error("删除失败");
	//}


}
