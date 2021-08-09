package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.Employee;
import com.example.server.pojo.Machine;
import com.example.server.pojo.MachineRecall;
import com.example.server.pojo.MachineTrace;
import com.example.server.service.impl.MachineRecallServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.MachineTraceServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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
 * @since 2021-07-30
 */
@RestController
@RequestMapping("/machine/recall")
public class MachineRecallController {

	@Autowired
	private MachineRecallServiceImpl machineRecallService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;
	@Autowired
	private MachineServiceImpl machineService;

	@ApiOperation("获取召回信息")
	@GetMapping("/")
	public RespBean getMachineRecall(@RequestParam(defaultValue = "1") Integer currentPage,
																	 @RequestParam(defaultValue = "10") Integer size,
																	 LocalDate[] bidDateScope,
																	 MachineRecall machineRecall) {
		RespPageBean purchaseOrder1 = machineRecallService.getMachineRecall(currentPage, size, machineRecall, bidDateScope);
		return RespBean.success("获取成功", purchaseOrder1);
	}

	@ApiOperation("添加召回信息")
	@PostMapping("/")
	@Transactional
	public RespBean addMachineRecall(@RequestBody MachineRecall[] machineRecalls, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		List<String> numbers = new ArrayList<>();

		try {
			LocalDateTime now = LocalDateTime.now();
			List<MachineTrace> machineTraceList = new ArrayList<>();

			for (MachineRecall machineRecall : machineRecalls) {
				numbers.add(machineRecall.getNumber());
				machineRecall.setNowOperateEmpId(empId);
				machineRecall.setRecallTime(now);

				MachineTrace machineTrace = new MachineTrace(machineRecall.getNumber(), machineRecall.getStatusId(), -1, now, machineRecall.getNowOperateEmpId(), "", machineRecall.getStorageLocationId(), machineRecall.getIsUpShelf());
				machineTrace.setIsRecall(1);
				machineTraceList.add(machineTrace);
			}

			if (machineService.update(new Machine(), new UpdateWrapper<Machine>().in("number", numbers).set("operate_emp_id", empId))) {
				if (machineRecallService.saveBatch(Arrays.asList(machineRecalls))) {
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
