package com.example.server.controller;


import com.example.server.pojo.EmployeeRole;
import com.example.server.service.impl.EmployeeRoleServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/employee/role")
public class EmployeeRoleController {

	@Autowired
	private EmployeeRoleServiceImpl employeeRoleService;

	@ApiOperation("获取角色-role")
	@GetMapping("/")
	public RespBean getEmpRoles() {
		//Map<Integer, List<Integer>> ans = new HashMap<>();
		//List<EmployeeRole> employeeRoles = employeeRoleService.list(null);
		//for (EmployeeRole employeeRole : employeeRoles) {
		//	if (!ans.containsKey(employeeRole.getRoleId())) {
		//		List<Integer> value = new ArrayList<>();
		//		value.add(employeeRole.getEmployeeId());
		//		ans.put(employeeRole.getRoleId(), value);
		//	} else {
		//		ans.get(employeeRole.getRoleId()).add(employeeRole.getEmployeeId());
		//	}
		//}
		return RespBean.success("获取成功", employeeRoleService.list(null));
	}

}
