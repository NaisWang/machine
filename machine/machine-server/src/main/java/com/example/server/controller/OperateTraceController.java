package com.example.server.controller;


import com.example.server.pojo.MachineRecall;
import com.example.server.pojo.OperateTrace;
import com.example.server.service.impl.OperateTraceServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-30
 */
@RestController
@RequestMapping("/operate-trace")
public class OperateTraceController {

	@Autowired
	private OperateTraceServiceImpl operateTraceService;


	@ApiOperation("获取召回信息")
	@GetMapping("/")
	public RespBean getOperateTrace(@RequestParam(defaultValue = "1") Integer currentPage,
																	@RequestParam(defaultValue = "10") Integer size,
																	LocalDate[] bidDateScope,
																	OperateTrace operateTrace) {
		RespPageBean purchaseOrder1 = operateTraceService.getOperateTrace(currentPage, size, operateTrace, bidDateScope);
		return RespBean.success("获取成功", purchaseOrder1);
	}
}
