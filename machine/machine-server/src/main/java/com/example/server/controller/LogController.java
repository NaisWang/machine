package com.example.server.controller;


import com.example.server.pojo.Log;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.instrument.classloading.weblogic.WebLogicLoadTimeWeaver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-16
 */
@RestController
@RequestMapping("/system/log")
public class LogController {

	@Autowired
	private LogServiceImpl logService;

	@ApiOperation("获取操作日志")
	@GetMapping("/")
	public RespBean getLog(@RequestParam(defaultValue = "1") Integer currentPage,
												 @RequestParam(defaultValue = "10") Integer size,
												 Log log,
												 LocalDate[] logTimeScope) {
		RespPageBean respPageBean = logService.getLog(currentPage, size, log, logTimeScope);
		return RespBean.success("获取成功", respPageBean);
	}

}
