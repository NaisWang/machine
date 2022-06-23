package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.MachineDetection;
import com.example.server.service.impl.MachineDetectionServiceImpl;
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
 * @since 2021-08-19
 */
@RestController
@RequestMapping("/machine/detection")
public class MachineDetectionController {

	@Autowired
	private MachineDetectionServiceImpl machineDetectionService;

	@ApiOperation("获取机器检测")
	@GetMapping("/")
	public RespBean getMachineDetection(MachineDetection machineDetection) {
		QueryWrapper<MachineDetection> machineDetectionQueryWrapper = new QueryWrapper<>();
		if (machineDetection.getMachineId() != null) {
			machineDetectionQueryWrapper.eq("machine_id", machineDetection.getMachineId());
		}
		List<MachineDetection> machineDetectionList = machineDetectionService.list(machineDetectionQueryWrapper);
		return RespBean.success("获取成功", machineDetectionList);
	}

}
