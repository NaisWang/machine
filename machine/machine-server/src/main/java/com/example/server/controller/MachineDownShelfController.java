package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.MachineDetectionServiceImpl;
import com.example.server.service.impl.MachineDownShelfServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.MachineTraceServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
@RequestMapping("/machine/down-shelf")
public class MachineDownShelfController {

	@Autowired
	private MachineDownShelfServiceImpl machineDownShelfService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@ApiOperation("获取召回信息")
	@GetMapping("/")
	public RespBean getMachineDownShelf(@RequestParam(defaultValue = "1") Integer currentPage,
																			@RequestParam(defaultValue = "10") Integer size,
																			LocalDate[] bidDateScope,
																			MachineDownShelf machineDownShelf) {
		RespPageBean pageBean = machineDownShelfService.getMachineDownShelf(currentPage, size, machineDownShelf, bidDateScope);
		return RespBean.success("获取成功", pageBean);
	}

	@ApiOperation("添加召回信息")
	@PostMapping("/")
	@Transactional
	public RespBean addMachineDownShelf(@RequestBody MachineDownShelf[] machineDownShelves, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();

		List<Integer> machineIds = new ArrayList<>();

		try {
			LocalDateTime now = LocalDateTime.now();
			List<MachineTrace> machineTraceList = new ArrayList<>();

			for (MachineDownShelf machineDownShelf : machineDownShelves) {

				machineIds.add(machineDownShelf.getMachineId());
				machineDownShelf.setRecallTime(now);

				MachineTrace machineTrace = new MachineTrace(machineDownShelf.getMachineId(), machineDownShelf.getMachineNumber(), 37, -1, now, empId, "", null, null);
				machineTrace.setIsRecall(1);
				machineTraceList.add(machineTrace);

			}

			if (machineService.update(new Machine(), new UpdateWrapper<Machine>().in("id", machineIds).set("operate_emp_id", empId).set("status_id", 37))) {
				if (machineDownShelfService.saveBatch(Arrays.asList(machineDownShelves))) {
					if (machineTraceService.saveBatch(machineTraceList)) {
						return RespBean.success("添加成功");
					}
				}
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

}
