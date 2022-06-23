package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.JudgeCompleteDeliverIntention;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.sun.tools.jconsole.JConsoleContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
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
	@Autowired
	private MachineTraceServiceImpl machineTraceService;
	@Autowired
	private OperateTraceServiceImpl operateTraceService;
	@Autowired
	private MachineDetectionServiceImpl machineDetectionService;
	@Autowired
	private MachineFixBillServiceImpl machineFixBillService;


	@ApiOperation("获取机器信息")
	@GetMapping("/")
	public RespBean getMachineInfo(@RequestParam(defaultValue = "1") Integer currentPage,
																 @RequestParam(defaultValue = "10") Integer size,
																 LocalDate[] bidDateScope,
																 Machine machine) {
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

	/**
	 * 判断一个机器是否修好
	 * 返回true，则说明维修成功
	 */
	private Boolean judgeFixSuccess(Machine machine) {
		List<String> tempFeatureDesc = Arrays.asList(machine.getFeatureDesc().split(","));
		List<String> featureDesc = new ArrayList<>(tempFeatureDesc);
		String[] notFixed = machine.getNotFixed() == null ? new String[]{} : machine.getNotFixed().split(",");
		for (String str : notFixed) {
			featureDesc.remove(str);
		}
		return featureDesc.size() == 0;
	}

	@ApiOperation("更改机器成色检测描述")
	@PutMapping("/modify/quality")
	@Transactional
	public RespBean modifyMachineQuality(@RequestBody Machine machine, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		try {
			Machine addMachine = new Machine();
			addMachine.setId(machine.getId());
			addMachine.setOperateEmpId(empId);

			Machine machine1 = machineService.getById(machine.getId());
			Integer nowStatusId = machine1.getStatusId();

			addMachine.setOperateDate(now);
			addMachine.setQualityDesc(machine.getQualityDesc());
			addMachine.setIsUpShelf(machine.getIsUpShelf());
			addMachine.setQualityComment(machine.getQualityComment());

			if (nowStatusId == 10) {
				addMachine.setStatusId(10);
			} else if (nowStatusId == 9 || nowStatusId == 38) {
				if (machine1.getFixTimes() != 0) {
					if (judgeFixSuccess(machine1)) {
						addMachine.setStatusId(10);
						machineFixBillService.save(new MachineFixBill(null, machine1.getId(), machine1.getNumber(), machine1.getSku(), machine1.getFixPrice(), 0f, now, empId, "", 0));
					} else {
						addMachine.setStatusId(38);
					}
				} else {
					addMachine.setStatusId(10);
				}
			} else {
				addMachine.setStatusId(8);
			}


			if (machineService.update(addMachine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), addMachine.getStatusId(), -1, now, empId, machine1.getComment(), machine.getStorageLocationId(), addMachine.getIsUpShelf());
				machineTrace.setFeatureComment(machine1.getFeatureComment());
				machineTrace.setQualityComment(addMachine.getQualityComment());
				if (machineTraceService.save(machineTrace)) {
					if (machineDetectionService.save(new MachineDetection(null, machine.getId(), addMachine.getStatusId(), machine.getNumber(), machine1.getQualityDesc(), machine1.getFeatureDesc(), machine1.getNeedFix(), empId, now, machine1.getComment(), addMachine.getQualityComment(), machine1.getFeatureComment(), machine1.getNeedFixComment()))) {
						if (operateTraceService.save(new OperateTrace(machine.getNumber(), machine.getId(), machine1.getSku(), 1, nowStatusId, addMachine.getStatusId(), now, empId, machine.getStorageLocationId()))) {
							List<Machine> machines = new ArrayList<>();
							machines.add(machine1);
							if (addMachine.getStatusId() == 10) {
								if (!JudgeCompleteDeliverIntention.judgeIsComplete(machines, 3)) {
									logService.save(new Log(empId, "更改机器成色检测描述", "机器id为" + machine.getId() + "机器成色检测设为" + machine.getQualityDesc(), now, 0));
									throw new RuntimeException("修改失败");
								}
							}
							if (!JudgeCompleteDeliverIntention.judgeIsComplete(machines, 12)) {
								logService.save(new Log(empId, "更改机器成色检测描述", "机器id为" + machine.getId() + "机器成色检测设为" + machine.getQualityDesc(), now, 0));
								throw new RuntimeException("修改失败");
							}
							if (!JudgeCompleteDeliverIntention.judgeIsComplete(machines, 4)) {
								logService.save(new Log(empId, "更改机器成色检测描述", "机器id为" + machine.getId() + "机器成色检测设为" + machine.getQualityDesc(), now, 0));
								throw new RuntimeException("修改失败");
							}
							return RespBean.success("修改成功", addMachine);
						}
					}
				}
			}
			throw new RuntimeException("修改失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "更改机器成色检测描述", "机器id为" + machine.getId() + "机器成色检测设为" + machine.getQualityDesc(), now, 1));
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("更改机器功能检测描述")
	@PutMapping("/modify/feature")
	@Transactional
	public RespBean modifyMachineFeature(@RequestBody Machine machine, Authentication authentication) {

		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		try {
			Machine addMachine = new Machine();
			addMachine.setId(machine.getId());
			addMachine.setOperateEmpId(empId);

			Machine machine1 = machineService.getById(machine.getId());
			Integer nowStatusId = machine1.getStatusId();

			addMachine.setOperateDate(now);
			addMachine.setFeatureDesc(machine.getFeatureDesc());
			addMachine.setFeatureComment(machine.getFeatureComment());
			addMachine.setPaijiBarcode(machine.getPaijiBarcode());


			if (nowStatusId == 10) {
				addMachine.setStatusId(10);
			} else if (nowStatusId == 8 || nowStatusId == 38) {
				if (machine1.getFixTimes() != 0) {
					addMachine.setNotFixed(machine1.getNotFixed());
					if (judgeFixSuccess(addMachine)) {
						addMachine.setStatusId(10);
						machineFixBillService.save(new MachineFixBill(null, machine1.getId(), machine1.getNumber(), machine1.getSku(), machine1.getFixPrice(), 0f, now, empId, "", 0));
					} else {
						addMachine.setStatusId(38);
					}
				} else {
					addMachine.setStatusId(10);
				}
			} else {
				addMachine.setStatusId(9);
			}

			if (machineService.update(addMachine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineDetectionService.save(new MachineDetection(null, machine.getId(), addMachine.getStatusId(), machine1.getNumber(), machine1.getQualityDesc(), addMachine.getFeatureDesc(), machine1.getNeedFix(), empId, now, machine1.getComment(), machine1.getQualityComment(), addMachine.getFeatureComment(), machine1.getNeedFixComment()))) {
					MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), addMachine.getStatusId(), -1, now, empId, machine1.getComment(), machine.getStorageLocationId(), machine1.getIsUpShelf());
					machineTrace.setFeatureComment(addMachine.getFeatureComment());
					machineTrace.setQualityComment(machine1.getQualityComment());
					if (machineTraceService.save(machineTrace)) {
						if (operateTraceService.save(new OperateTrace(machine.getNumber(), machine.getId(), machine1.getSku(), 2, nowStatusId, addMachine.getStatusId(), now, empId, machine.getStorageLocationId()))) {
							List<Machine> machines = new ArrayList<>();
							machines.add(machine1);
							if (addMachine.getStatusId() == 10) {
								if (!JudgeCompleteDeliverIntention.judgeIsComplete(machines, 3)) {
									logService.save(new Log(empId, "更改机器成色检测描述", "机器id为" + machine.getId() + "机器成色检测设为" + machine.getQualityDesc(), now, 0));
									throw new RuntimeException("修改失败");
								}
							}
							if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 5)) {
								logService.save(new Log(empId, "更改机器成色检测描述", "机器id为" + machine.getId() + "机器成色检测设为" + machine.getQualityDesc(), now, 0));
								return RespBean.success("修改成功", addMachine);
							}
						}
					}
				}
			}
			throw new RuntimeException("修改失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("确定机器维修项")
	@PutMapping("/modify/needFix")
	@Transactional
	public RespBean modifyMachineFixItem(@RequestBody Machine machine, Authentication authentication) {

		List<String> logs = new ArrayList<>();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		Machine machine1 = machineService.getById(machine.getId());

		try {
			Machine addMachine = new Machine();

			addMachine.setOperateEmpId(empId);
			addMachine.setStatusId(32);
			addMachine.setOperateDate(now);
			addMachine.setNeedFix(machine.getNeedFix());
			addMachine.setComment(machine.getComment());

			if (machineService.update(addMachine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineDetectionService.save(new MachineDetection(null, machine.getId(), addMachine.getStatusId(), machine1.getNumber(), machine1.getQualityDesc(), machine1.getFeatureDesc(), addMachine.getNeedFix(), empId, now, machine1.getComment(), machine1.getQualityComment(), machine1.getFeatureComment(), addMachine.getComment()))) {
					if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), addMachine.getStatusId(), -1, now, empId, addMachine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
						if (operateTraceService.save(new OperateTrace(machine.getNumber(), machine.getId(), machine.getSku(), 4, machine.getStatusId(), 32, now, empId, machine.getStorageLocationId()))) {
							logService.save(new Log(empId, "确定机器维修项", "机器number为" + machine.getNumber(), now, 0));
							return RespBean.success("修改成功", addMachine);
						}
					}
				}
			}
			logService.save(new Log(empId, "确定机器维修项", "机器number为" + machine.getNumber(), now, 1));
			throw new RuntimeException("修改失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "确定机器维修项", "机器number为" + machine.getNumber(), now, 1));
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("维修完成")
	@PutMapping("/modify/fixComplete")
	@Transactional
	public RespBean modifyMachineFixComplete(@RequestBody Machine machine, Integer type, Authentication authentication) {

		List<String> logs = new ArrayList<>();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		try {
			Machine addMachine = new Machine();
			addMachine.setId(machine.getId());
			addMachine.setOperateEmpId(empId);
			if (type == 0) {
				addMachine.setStatusId(33);
				Machine machine1 = machineService.getById(machine.getId());
				addMachine.setFixTimes(machine1.getFixTimes() + 1);
			} else if (type == 1) {
				addMachine.setStatusId(16);
			}

			addMachine.setOperateDate(now);
			addMachine.setFixed(machine.getFixed());
			addMachine.setNotFixed(machine.getNotFixed());
			addMachine.setFixToBad(machine.getFixToBad());
			addMachine.setFixPrice(machine.getFixPrice());
			addMachine.setComment(machine.getComment());

			if (machineService.update(addMachine, new UpdateWrapper<Machine>().eq("number", machine.getNumber()))) {
				if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), addMachine.getStatusId(), -1, now, empId, addMachine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
					if (operateTraceService.save(new OperateTrace(machine.getNumber(), machine.getId(), machine.getSku(), 5, machine.getStatusId(), addMachine.getStatusId(), now, empId, machine.getStorageLocationId()))) {
						List<Machine> machines = new ArrayList<>();
						Machine machine1 = machineService.getById(machine.getId());
						machines.add(machine1);
						if (type == 0) {
							if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 10)) {
								logService.save(new Log(empId, "维修完成", "机器number：" + machine.getNumber(), now, 0));
								return RespBean.success("修改成功", addMachine);
							}
						} else {
							logService.save(new Log(empId, "维修完成", "机器number：" + machine.getNumber(), now, 0));
							return RespBean.success("修改成功", addMachine);
						}
					}
				}
			}
			logService.save(new Log(empId, "维修完成", "机器number：" + machine.getNumber(), now, 1));
			throw new RuntimeException("修改失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "维修完成", "机器number：" + machine.getNumber(), now, 1));
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("更改机器的上架信息")
	@PutMapping("/modify/upShelf")
	@Transactional
	public RespBean modifyMachineUpShelf(@RequestBody Machine machine, Integer type, Authentication authentication) {

		//状态判断是否合法
		Machine machine1 = machineService.getOne(new QueryWrapper<Machine>().eq("number", machine.getNumber()));
		Integer nowStatusId = machine1.getStatusId();
		if (nowStatusId != 10 && nowStatusId != 11 && nowStatusId != 36) {
			return RespBean.error("数据非法");
		}

		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		try {
			Machine addMachine = new Machine();
			addMachine.setId(machine.getId());
			addMachine.setOperateEmpId(empId);
			if (type == 0) {
				addMachine.setStatusId(11);
			} else {
				addMachine.setStatusId(36);
			}
			addMachine.setOperateDate(now);
			addMachine.setGoodPrice(machine.getGoodPrice());
			addMachine.setBidPrice(machine.getBidPrice());
			addMachine.setOnePrice(machine.getOnePrice());
			addMachine.setRankDesc(machine.getRankDesc());
			addMachine.setBagNumber(machine.getBagNumber());
			addMachine.setComment(machine.getComment());
			if (machineService.update(addMachine, new UpdateWrapper<Machine>().eq("number", machine.getNumber()))) {
				if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), addMachine.getStatusId(), -1, now, empId, addMachine.getComment(), machine.getStorageLocationId(), machine1.getIsUpShelf()))) {
					if (operateTraceService.save(new OperateTrace(machine.getNumber(), machine.getId(), machine1.getSku(), 3, machine.getStatusId(), addMachine.getStatusId(), now, empId, machine.getStorageLocationId()))) {
						List<Machine> machines = new ArrayList<>();
						machines.add(machine1);
						if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 6)) {
							logService.save(new Log(empId, "更改机器上架信息", "机器id为" + machine.getId() + "。一口价：" + machine.getOnePrice() + "; 最高价：" + machine.getBidPrice() + "; 好的价格：" + machine.getGoodPrice() + "; 机器等级：" + machine.getRankDesc() + "; 袋子编号：" + machine.getBagNumber(), now, 0));
							return RespBean.success("修改成功", addMachine);
						}
					}
				}
			}
			throw new RuntimeException("修改失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("确定机器是否可以上架")
	@PutMapping("/modify/canUpShelf")
	@Transactional
	public RespBean modifyMachineCanUpShelf(@RequestBody Machine[] machines, Authentication authentication, Integer type) {

		List<String> logs = new ArrayList<>();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		List<MachineTrace> machineTraces = new ArrayList<>();
		List<Machine> addMachines = new ArrayList<>();

		try {
			for (Machine machine : machines) {
				Machine addMachine = new Machine();

				addMachine.setId(machine.getId());
				addMachine.setOperateDate(now);
				addMachine.setIsUpShelf(type);
				addMachine.setStatusId(machine.getStatusId());
				addMachines.add(addMachine);

				MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), addMachine.getStatusId(), -1, now, empId, addMachine.getComment(), machine.getStorageLocationId(), type);
				machineTraces.add(machineTrace);
			}

			if (machineService.updateBatchById(addMachines)) {
				if (machineTraceService.saveBatch(machineTraces)) {
					logService.save(new Log(empId, "确定是否可以上架", "", now, 0));
					return RespBean.success("修改成功");
				}
			}
			logService.save(new Log(empId, "确定机器是否可以上架", "", now, 1));
			throw new RuntimeException("修改失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "确定机器是否可以上架", "", now, 1));
			throw new RuntimeException("修改失败");
		}
	}

	@ApiOperation("更改机器的状态信息")
	@PutMapping("/modify/status-to5")
	@Transactional
	public RespBean modifyMachineStatusTo5(@RequestBody Machine[] machines, Authentication authentication) {

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

