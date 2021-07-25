package com.example.server.controller;


import com.example.server.pojo.Employee;
import com.example.server.pojo.Role;
import com.example.server.service.impl.PurchaseOrderModifyServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-05-29
 */
@RestController
@RequestMapping("/machine/general/purchase-order-modify")
public class PurchaseOrderModifyController {

	@Autowired
	private PurchaseOrderModifyServiceImpl purchaseOrderModifyService;

	@ApiOperation("判断当前用户能修改采购单中哪些字段")
	@GetMapping("/")
	public RespBean isModifyFiled(Authentication authentication) {
		Employee employee = (Employee) authentication.getPrincipal();
		List<Role> roleList = employee.getRoles();
		StringBuffer sb = new StringBuffer();
		for (Role role : roleList) {
			sb.append(role.getId() + ",");
		}
		return RespBean.success("操作成功", purchaseOrderModifyService.isModifyFiled(sb.substring(0, sb.length() - 1)));
	}

}
