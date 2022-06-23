package com.example.server.controller;

import com.example.server.pojo.Finance;
import com.example.server.service.impl.FinanceServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : whz
 */
@RestController
@Component
@RequestMapping("/system/finance")
public class FinanceController {

	@Autowired
	private FinanceServiceImpl financeService;

	@ApiOperation("获取财务情况")
	@GetMapping("/")
	public RespBean getFinance() {
		Finance finance = financeService.getFinance();
		return RespBean.success("获取成功", finance);
	}
}
