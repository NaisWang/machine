package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.Employee;
import com.example.server.pojo.Log;
import com.example.server.pojo.Machine;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
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
 * @since 2021-07-07
 */
@RestController
@RequestMapping("/machine")
public class MachineController {

	@Autowired
	private MachineServiceImpl machineService;

	@Autowired
	private LogServiceImpl logService;

	@ApiOperation("获取机器信息")
	@GetMapping("/")
	public RespBean getMachineInfo(@RequestParam(defaultValue = "1") Integer currentPage,
																 @RequestParam(defaultValue = "10") Integer size,
																 LocalDate[] bidDateScope,
																 Machine machine) {
		System.out.println("aaa");
		System.out.println(machine);
		RespPageBean purchaseOrder1 = machineService.getMachine(currentPage, size, machine, bidDateScope);
		return RespBean.success("获取成功", purchaseOrder1);
	}

	@ApiOperation("更改机器信息")
	@PutMapping("/")
	@Transactional
	public RespBean modifyMachine(@RequestBody Machine[] machines, Authentication authentication) {
		System.out.println(Arrays.toString(machines));
		if (machineService.updateBatchById(Arrays.asList(machines))) {
			logService.save(new Log(((Employee) authentication.getPrincipal()).getId(), "更改机器的信息", Arrays.toString(machines), LocalDateTime.now(), 0));
			return RespBean.success("修改成功");
		}
		logService.save(new Log(((Employee) authentication.getPrincipal()).getId(), "更改机器的信息", Arrays.toString(machines), LocalDateTime.now(), 1));
		throw new RuntimeException("修改失败");
	}


	@ApiOperation("更改机器成色检测描述")
	@PutMapping("/modify/quality")
	@Transactional
	public RespBean modifyMachineQuality(@RequestBody Machine[] machines, Authentication authentication) {

		List<String> logs = new ArrayList<>();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		try {
			for (Machine machine : machines) {
				machine.setOperateEmpId(empId);
				machine.setStatusId(14);
				machine.setOperateDate(now);
				machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()).set("quality_desc", machine.getQualityDesc()).set("comment", machine.getComment()));
				logs.add("机器id为" + machine.getId() + "机器成色检测设为" + machine.getQualityDesc());
			}
			for (String log : logs) {
				logService.save(new Log(empId, "更改机器成色检测描述", log, now, 0));
			}
			return RespBean.success("修改成功", machines);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("更改机器功能检测描述")
	@PutMapping("/modify/feature")
	@Transactional
	public RespBean modifyMachineFeature(@RequestBody Machine[] machines, Authentication authentication) {

		List<String> logs = new ArrayList<>();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		try {
			for (Machine machine : machines) {
				machine.setOperateEmpId(empId);
				machine.setStatusId(15);
				machine.setOperateDate(now);
				machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()).set("feature_desc", machine.getFeatureDesc()).set("comment", machine.getComment()).set("paiji_barcode", machine.getPaijiBarcode()));
				logs.add("机器id为" + machine.getId() + "机器功能检测设为" + machine.getQualityDesc());
			}
			for (String log : logs) {
				logService.save(new Log(empId, "更改机器功能检测描述", log, now, 0));
			}
			return RespBean.success("修改成功", machines);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("更改机器的上架信息")
	@PutMapping("/modify/upShelf")
	@Transactional
	public RespBean modifyMachineUpShelf(@RequestBody Machine[] machines, Authentication authentication){

		List<String> logs = new ArrayList<>();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		try {
			for (Machine machine : machines) {
				machine.setOperateEmpId(empId);
				machine.setStatusId(15);
				machine.setOperateDate(now);
				machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()).set("feature_desc", machine.getFeatureDesc()).set("comment", machine.getComment()).set("paiji_barcode", machine.getPaijiBarcode()));
				logs.add("机器id为" + machine.getId() + "。一口价：" + machine.getOnePrice() + "; 最高价：" + machine.getBidPrice() + "; 好的价格：" + machine.getGoodPrice() + "; 机器等级：" + machine.getRankDesc() + "; 袋子编号：" + machine.getBagNumber());
			}
			for (String log : logs) {
				logService.save(new Log(empId, "更改机器上架信息", log, now, 0));
			}
			return RespBean.success("修改成功", machines);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("更改机器的状态信息")
	@PutMapping("/modify/status-to5")
	@Transactional
	public RespBean modifyMachineStatusTo5(@RequestBody Machine[] machines, Authentication authentication){

		List<String> logs = new ArrayList<>();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		try {
			for (Machine machine : machines) {
				machine.setOperateEmpId(empId);
				machine.setStatusId(5);
				machine.setOperateDate(now);
				machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()));
				logs.add("机器id为" + machine.getId() + "状态变为处理中");
			}
			for (String log : logs) {
				logService.save(new Log(empId, "机器状态变为处理中", log, now, 0));
			}
			return RespBean.success("修改成功", machines);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改失败");
		}
	}

}

