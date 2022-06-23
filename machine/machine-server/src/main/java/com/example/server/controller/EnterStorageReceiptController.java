package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.Corr;
import com.example.server.utils.JudgeCompleteDeliverIntention;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Update;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-09
 */
@RestController
@RequestMapping("/machine/storage/enter-storage")
public class EnterStorageReceiptController {

	@Autowired
	private EnterStorageReceiptServiceImpl enterStorageReceiptService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;
	@Autowired
	private DeliverMachineServiceImpl deliverMachineService;
	@Autowired
	private DeliverReceiptServiceImpl deliverReceiptService;
	@Autowired
	private EnterStorageReceiptToMachineServiceImpl enterStorageReceiptToMachineService;

	@ApiModelProperty("获取所有入库单据")
	@GetMapping("/")
	public RespBean getEnterStorageReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																				 @RequestParam(defaultValue = "10") Integer size,
																				 EnterStorageReceipt enterStorageReceipt) {
		RespPageBean respPageBean = enterStorageReceiptService.getEnterStorageReceipt(currentPage, size, enterStorageReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiModelProperty("获取入库单据中的机器")
	@GetMapping("/machines")
	public RespBean getEnterStorageReceiptToMachine(@RequestParam(defaultValue = "1") Integer currentPage,
																									@RequestParam(defaultValue = "10") Integer size,
																									Integer receiptId) {
		RespPageBean respPageBean = enterStorageReceiptToMachineService.getEnterStorageReceiptToMachine(currentPage, size, receiptId);
		return RespBean.success("获取成功", respPageBean);
	}

	//@ApiModelProperty("创建入库单")
	//@PutMapping("/")
	//@Transactional
	//public RespBean createEnterStorageReceipt(@RequestBody Map<String, String> enterStorageReceiptMachine) throws Exception {
	//	ObjectMapper objectMapper = new ObjectMapper();

	//	EnterStorageReceipt enterStorageReceipt = objectMapper.readValue(enterStorageReceiptMachine.get("enterStorageReceipt"), EnterStorageReceipt.class);
	//	List<Machine> machines = objectMapper.readValue(enterStorageReceiptMachine.get("machine"), new TypeReference<List<Machine>>() {
	//	});

	//	LocalDateTime nowDateTime = LocalDateTime.now();
	//	LocalDate nowDate = LocalDate.now();
	//	enterStorageReceipt.setEnterStorageDate(nowDate);

	//	if (enterStorageReceiptService.save(enterStorageReceipt)) {
	//		Integer enterStorageReceiptId = enterStorageReceipt.getEnterStorageOrder();
	//		for (Machine machine : machines) {
	//			machine.setEnterStorageReceiptId(enterStorageReceiptId);
	//			machine.setEnterStorageDate(nowDateTime);
	//			machine.setStatusId(2);
	//		}
	//		if (machineService.updateBatchById(machines)) {
	//			return RespBean.success("添加成功");
	//		}
	//	}
	//	throw new RuntimeException("添加失败");
	//}


	@ApiOperation("创建入库单")
	@PutMapping("/")
	@Transactional
	public RespBean createEnterStorageReceipt(@RequestBody EnterStorageReceipt enterStorageReceipt, Authentication authentication) throws Exception {
		if (enterStorageReceipt.getStorageLocationId() == null) {
			return RespBean.success("没有选择库位!!!");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			enterStorageReceipt.setCreateTime(now);
			enterStorageReceipt.setOperateEmpId(empId);
			enterStorageReceipt.setIsRelease(0);
			if (enterStorageReceiptService.save(enterStorageReceipt)) {
				logService.save(new Log(empId, "添加入库单", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 0));
				return RespBean.success("添加成功");
			}
			logService.save(new Log(empId, "添加入库单", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "添加入库单", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("往入库单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		EnterStorageReceipt enterStorageReceipt = enterStorageReceiptService.getById(receiptId);
		try {

			if (!empId.equals(enterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (enterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该入库单已经发布了");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));
			List<MachineTrace> machineTraceList = new ArrayList<>();
			List<EnterStorageReceiptToMachine> enterStorageReceiptToMachines = new ArrayList<>();

			//Integer[] legalStatusId = {1, 3, 4};

			for (Machine machine : machines) {
				//if (!Arrays.asList(legalStatusId).contains(machine.getStatusId())) {
				//		return RespBean.error("数据有误！！！");
				//	}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(20);
				machine.setEnterStorageReceiptId(receiptId);
				machine.setOperateEmpId(empId);
				machineTraceList.add(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));

				enterStorageReceiptToMachines.add(new EnterStorageReceiptToMachine(null, receiptId, machine.getId(), machine.getNumber(), machine.getSku()));
			}

			if (machineService.updateBatchById(machines)) {
				if (enterStorageReceiptToMachineService.saveBatch(enterStorageReceiptToMachines)) {
					if (machineTraceService.saveBatch(machineTraceList)) {
						logService.save(new Log(empId, "往入库单中添加机器", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 0));
						return RespBean.success("添加成功");
					}
				}
			}
			logService.save(new Log(empId, "往入库单中添加机器", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "往入库单中添加机器", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("发布入库单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseEnterStorageReceipt(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		EnterStorageReceipt enterStorageReceipt = enterStorageReceiptService.getById(receiptId);
		try {
			if (!empId.equals(enterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (enterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该入库单已经发布了");
			}

			List<Machine> machines = machineService.list(new UpdateWrapper<Machine>().eq("enter_storage_receipt_id", receiptId));
			if (machines.size() == 0) {
				return RespBean.error("该入库单为空，不能发布");
			}

			if (enterStorageReceiptService.update(new EnterStorageReceipt(), new UpdateWrapper<EnterStorageReceipt>().eq("enter_storage_order", receiptId).set("is_release", 1).set("release_time", now))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("enter_storage_receipt_id", receiptId).set("status_id", 2).set("enter_storage_date", now).set("enter_storage_emp_id", empId).set("storage_location_id", enterStorageReceipt.getStorageLocationId()))) {
					List<MachineTrace> machineTraces = new ArrayList<>();
					//跟踪机器数据
					for (Machine machine : machines) {

						MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), 2, receiptId, now, empId, machine.getComment(), enterStorageReceipt.getStorageLocationId(), machine.getIsUpShelf());

						machineTraces.add(machineTrace);
					}
					if (machineTraceService.saveBatch(machineTraces)) {
						if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 2)) {
							logService.save(new Log(empId, "发布入库单", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 0));
							return RespBean.success("发布成功");
						}
					}
				}
			}
			logService.save(new Log(empId, "发布入库单", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "发布入库单", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("发布失败");
		}
	}


	@ApiOperation("删除入库单中的机器")
	@DeleteMapping("/deleteMachine/")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Machine machine = machineService.getById(id);
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		EnterStorageReceipt enterStorageReceipt = enterStorageReceiptService.getById(machine.getEnterStorageReceiptId());
		try {
			if (!empId.equals(enterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			//if (enterStorageReceipt.getIsRelease() == 1) {
			//		return RespBean.error("该入库单已经发布了");
			//	}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setEnterStorageReceiptId(0);
			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (enterStorageReceiptToMachineService.remove(new QueryWrapper<EnterStorageReceiptToMachine>().eq("receipt_id", enterStorageReceipt.getEnterStorageOrder()).eq("machine_number", machine.getNumber()))) {
					if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), -1, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
						logService.save(new Log(empId, "删除入库单中添加机器", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除入库单中添加机器", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除入库单中添加机器", "入库单据id为" + enterStorageReceipt.getEnterStorageOrder() + "; 备注是：" + enterStorageReceipt.getComment(), now, 1));
			throw new RuntimeException("删除失败");
		}
	}


	@ApiModelProperty("一键入库")
	@PutMapping("/one-key-enter/")
	@Transactional
	public RespBean createEnterStorageReceiptByOneKey(@RequestBody Map<String, String> enterStorageReceiptMachine, Authentication authentication) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			EnterStorageReceipt enterStorageReceipt = objectMapper.readValue(enterStorageReceiptMachine.get("enterStorageReceipt"), EnterStorageReceipt.class);
			Integer purchaseOrder = Integer.parseInt(enterStorageReceiptMachine.get("purchaseOrder"));

			LocalDateTime nowDateTime = LocalDateTime.now();
			enterStorageReceipt.setCreateTime(nowDateTime);
			enterStorageReceipt.setReleaseTime(nowDateTime);
			enterStorageReceipt.setIsRelease(1);
			enterStorageReceipt.setOperateEmpId(empId);

			List<Machine> machineList = machineService.list(new QueryWrapper<Machine>().eq("purchase_order_id", purchaseOrder));
			List<EnterStorageReceiptToMachine> enterStorageReceiptToMachines = new ArrayList<>();

			if (enterStorageReceiptService.save(enterStorageReceipt)) {
				for (Machine machine : machineList) {
					enterStorageReceiptToMachines.add(new EnterStorageReceiptToMachine(null, enterStorageReceipt.getEnterStorageOrder(), machine.getId(), machine.getNumber(), machine.getSku()));
				}
				if (enterStorageReceiptToMachineService.saveBatch(enterStorageReceiptToMachines)) {
					if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("purchase_order_id", purchaseOrder).set("storage_location_id", enterStorageReceipt.getStorageLocationId()).set("status_id", 2).set("enter_storage_receipt_id", enterStorageReceipt.getEnterStorageOrder()).set("enter_storage_date", now).set("enter_storage_emp_id", empId))) {
						List<MachineTrace> machineTraces = new ArrayList<>();
						for (Machine machine : machineList) {
							MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), 2, enterStorageReceipt.getEnterStorageOrder(), now, empId, machine.getComment(), enterStorageReceipt.getStorageLocationId(), machine.getIsUpShelf());
							machineTraces.add(machineTrace);
						}
						if (machineTraceService.saveBatch(machineTraces)) {
							logService.save(new Log(empId, "一键入库", "采购单号为" + purchaseOrder, nowDateTime, 0));
							return RespBean.success("添加成功");
						}
					}
				}
			}
			logService.save(new Log(empId, "一键入库", "采购单号为" + purchaseOrder, nowDateTime, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiModelProperty("一键库位调拨")
	@PutMapping("/one-key-transfer/")
	@Transactional
	public RespBean createEnterStorageReceiptByOneKeyTransfer(@RequestBody Map<String, String> enterStorageReceiptMachine, Authentication authentication) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			EnterStorageReceipt enterStorageReceipt = objectMapper.readValue(enterStorageReceiptMachine.get("enterStorageReceipt"), EnterStorageReceipt.class);
			Integer originEnterStorageReceipt = Integer.parseInt(enterStorageReceiptMachine.get("originEnterStorageReceipt"));

			LocalDateTime nowDateTime = LocalDateTime.now();
			enterStorageReceipt.setCreateTime(nowDateTime);
			enterStorageReceipt.setReleaseTime(nowDateTime);
			enterStorageReceipt.setIsRelease(1);
			enterStorageReceipt.setOperateEmpId(empId);

			List<Machine> machineList = machineService.list(new QueryWrapper<Machine>().eq("enter_storage_receipt_id", originEnterStorageReceipt));
			List<EnterStorageReceiptToMachine> enterStorageReceiptToMachines = new ArrayList<>();

			if (enterStorageReceiptService.save(enterStorageReceipt)) {
				for (Machine machine : machineList) {
					enterStorageReceiptToMachines.add(new EnterStorageReceiptToMachine(null, enterStorageReceipt.getEnterStorageOrder(), machine.getId(), machine.getNumber(), machine.getSku()));
				}
				if (enterStorageReceiptToMachineService.saveBatch(enterStorageReceiptToMachines)) {
					if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("enter_storage_receipt_id", originEnterStorageReceipt).set("storage_location_id", enterStorageReceipt.getStorageLocationId()).set("status_id", 2).set("enter_storage_receipt_id", enterStorageReceipt.getEnterStorageOrder()).set("enter_storage_date", now).set("enter_storage_emp_id", empId))) {
						List<MachineTrace> machineTraces = new ArrayList<>();
						for (Machine machine : machineList) {
							MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), 2, enterStorageReceipt.getEnterStorageOrder(), now, empId, machine.getComment(), enterStorageReceipt.getStorageLocationId(), machine.getIsUpShelf());
							machineTraces.add(machineTrace);
						}
						if (machineTraceService.saveBatch(machineTraces)) {
							logService.save(new Log(empId, "一键库位调拨", "", nowDateTime, 0));
							return RespBean.success("添加成功");
						}
					}
				}

			}
			logService.save(new Log(empId, "一键库位调拨", "", nowDateTime, 1));
			throw new RuntimeException("添加失败");
		} catch (
				Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}

	}


	@ApiOperation("修改入库单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody EnterStorageReceipt receipt, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();

		if (!empId.equals(receipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}

		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该采购单已添加");
		}


		EnterStorageReceipt afterEnterStorageReceipt = new EnterStorageReceipt();
		afterEnterStorageReceipt.setComment(receipt.getComment());

		if (enterStorageReceiptService.update(afterEnterStorageReceipt, new UpdateWrapper<EnterStorageReceipt>().eq("enter_storage_order", receipt.getEnterStorageOrder()))) {
			logService.save(new Log(empId, "修改入库单信息", "入库单据id为" + receipt.getEnterStorageOrder() + "; 备注是：" + receipt.getComment(), now, 0));
			return RespBean.success("更新成功");
		}
		logService.save(new Log(empId, "修改入库单信息", "入库单据id为" + receipt.getEnterStorageOrder() + "; 备注是：" + receipt.getComment(), now, 1));
		return RespBean.error("更新失败");
	}

	@ApiOperation("删除入库单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId, Authentication authentication) {

		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		try {
			if (receiptId == 0) {
				return RespBean.error("数据错误");
			}
			EnterStorageReceipt enterStorageReceipt = enterStorageReceiptService.getById(receiptId);
			if (!empId.equals(enterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (enterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该入库单中的数据已经修改了, 无法再删除");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("enter_storage_receipt_id", receiptId));
			List<MachineTrace> machineTraceList = new ArrayList<>();

			for (Machine machine : machines) {
				machine.setPurchaseReturnReceiptId(0);
				machine.setStatusId(machine.getPreviousStatusId());
				machineTraceList.add(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
			}
			if (enterStorageReceiptService.removeById(receiptId)) {
				if (machines.size() == 0 || machineService.updateBatchById(machines)) {
					if (enterStorageReceiptToMachineService.remove(new QueryWrapper<EnterStorageReceiptToMachine>().eq("receipt_id", receiptId))) {
						if (machineTraceService.saveBatch(machineTraceList)) {
							logService.save(new Log(empId, "删除入库单信息", "入库单据id为" + receiptId, now, 0));
							return RespBean.success("删除成功");
						}
					}
				}
			}
			logService.save(new Log(empId, "删除入库单信息", "入库单据id为" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除入库单信息", "入库单据id为" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("创建、添加、发布入库单")
	@PutMapping("/create-add-release")
	@Transactional
	public RespBean createAddRelease(@RequestBody String[] numbers, Integer enterStorageReceiptId, Integer storageLocationId, Integer deliverReceiptId, Authentication authentication) {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();

		if (storageLocationId == null) {
			return RespBean.error("没有选择库位");
		}

		try {
			//如果没有入库单，则创建并发布入库单
			if (enterStorageReceiptId == null) {
				EnterStorageReceipt enterStorageReceipt = new EnterStorageReceipt();
				enterStorageReceipt.setCreateTime(now);
				enterStorageReceipt.setReleaseTime(now);
				enterStorageReceipt.setOperateEmpId(empId);
				enterStorageReceipt.setIsRelease(1);
				enterStorageReceipt.setStorageLocationId(storageLocationId);
				if (enterStorageReceiptService.save(enterStorageReceipt)) {
					enterStorageReceiptId = enterStorageReceipt.getEnterStorageOrder();
				} else {
					throw new RuntimeException("运行错误");
				}
			}

			DeliverReceipt deliverReceipt = deliverReceiptService.getById(deliverReceiptId);
			deliverReceipt.setStorageLocationId(storageLocationId);
			deliverReceipt.setEnterStorageReceiptId(enterStorageReceiptId);
			if (!deliverReceiptService.update(deliverReceipt, new UpdateWrapper<DeliverReceipt>().eq("deliver_receipt_id", deliverReceiptId))) {
				throw new RuntimeException("运行错误");
			}

			//接收并处理机器
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("number", numbers));
			List<MachineTrace> machineTraces = new ArrayList<>();
			List<EnterStorageReceiptToMachine> enterStorageReceiptToMachines = new ArrayList<>();

			QueryWrapper<DeliverMachine> deliverMachineQueryWrapper = new QueryWrapper<>();
			for (Machine machine : machines) {

				if (!machine.getDeliverReceiptId().equals(deliverReceiptId)) {
					throw new RuntimeException("运行错误");
				}

				machine.setEnterStorageReceiptId(enterStorageReceiptId);
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(2);
				machine.setDeliverReceiptId(0);
				machine.setNeedCompleteDeliverReceiptId(0);
				machine.setOperateEmpId(empId);

				enterStorageReceiptToMachines.add(new EnterStorageReceiptToMachine(null, enterStorageReceiptId, machine.getId(), machine.getNumber(), machine.getSku()));


				Map<String, Integer> queryMap = new HashMap<>();
				queryMap.put("machine_id", machine.getId());
				queryMap.put("deliver_receipt_id", deliverReceiptId);
				deliverMachineQueryWrapper = deliverMachineQueryWrapper.or().allEq(queryMap);

				MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), 2, enterStorageReceiptId, now, empId, machine.getComment(), storageLocationId, machine.getIsUpShelf());
				machineTraces.add(machineTrace);

			}

			List<DeliverMachine> deliverMachines = deliverMachineService.list(deliverMachineQueryWrapper);
			for (DeliverMachine deliverMachine : deliverMachines) {
				deliverMachine.setReceiveEmpId(empId);
				deliverMachine.setStatus(2);
				deliverMachine.setIsComplete(1);
				deliverMachine.setReceiveDate(now);
			}

			if (machineService.updateBatchById(machines)) {
				if (enterStorageReceiptToMachineService.saveBatch(enterStorageReceiptToMachines)) {
					if (deliverMachineService.updateBatchById(deliverMachines)) {
						if (machineTraceService.saveBatch(machineTraces)) {
							return RespBean.success("操作成功", enterStorageReceiptId);
						}
					}
				}
			}
			throw new RuntimeException("操作失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("操作失败");
		}
	}
}
