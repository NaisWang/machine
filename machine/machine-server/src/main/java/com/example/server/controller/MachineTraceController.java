package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.MachineTrace;
import com.example.server.service.impl.MachineTraceServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @since 2021-07-27
 */
@RestController
@RequestMapping("/machine-trace")
public class MachineTraceController {

	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@ApiOperation("获取机器追踪")
	@GetMapping("/")
	public RespBean getMachineTrace(MachineTrace machineTrace) {
		QueryWrapper<MachineTrace> machineTraceQueryWrapper = new QueryWrapper<>();
		if (machineTrace.getMachineId() != null) {
			machineTraceQueryWrapper.eq("machine_id", machineTrace.getMachineId());
		}
		List<MachineTrace> machineTraces = machineTraceService.list(machineTraceQueryWrapper);
		return RespBean.success("获取成功", machineTraces);
	}

}
