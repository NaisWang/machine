package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.Corr;
import com.example.server.utils.JudgeCompleteDeliverIntention;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-11
 */
@RestController
@RequestMapping("/machine/purchase/return")
public class PurchaseReturnReceiptController {

	@Autowired
	private PurchaseReturnReceiptServiceImpl purchaseReturnReceiptService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;
	@Autowired
	private PurchaseReturnReceiptToMachineServiceImpl purchaseReturnReceiptToMachineService;

	@ApiOperation("获取所有采购退货单据")
	@GetMapping("/")
	public RespBean getPurchaseReturnReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																					 @RequestParam(defaultValue = "10") Integer size,
																					 PurchaseReturnReceipt purchaseReturnReceipt) {
		RespPageBean respPageBean = purchaseReturnReceiptService.getPurchaseReturnReceipt(currentPage, size, purchaseReturnReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiOperation("获取采购退货单据中的机器")
	@GetMapping("/machines")
	public RespBean getPurchaseReturnReceiptToMachine(@RequestParam(defaultValue = "1") Integer currentPage,
																										@RequestParam(defaultValue = "10") Integer size,
																										Integer receiptId) {
		RespPageBean respPageBean = purchaseReturnReceiptToMachineService.getPurchaseReturnReceiptToMachine(currentPage, size, receiptId);
		return RespBean.success("获取成功", respPageBean);
	}


	//@ApiModelProperty("创建采购退货单")
	//@PutMapping("/")
	//@Transactional
	//public RespBean createPurchaseReturnReceipt(@RequestBody Map<String, String> purchaseReturnReceiptMachine, Authentication authentication) throws Exception {
	//	ObjectMapper objectMapper = new ObjectMapper();

	//	PurchaseReturnReceipt purchaseReturnReceipt = objectMapper.readValue(purchaseReturnReceiptMachine.get("purchaseReturnReceipt"), PurchaseReturnReceipt.class);
	//	List<Machine> machines = objectMapper.readValue(purchaseReturnReceiptMachine.get("machine"), new TypeReference<List<Machine>>() {
	//	});

	//	System.out.println(purchaseReturnReceipt);
	//	System.out.println(machines);

	//	LocalDate nowDate = LocalDate.now();
	//	purchaseReturnReceipt.setPurchaseReturnDate(nowDate);
	//	Integer empId = ((Employee) authentication.getPrincipal()).getId();

	//	if (purchaseReturnReceiptService.save(purchaseReturnReceipt)) {
	//		Integer purchaseReturnReceiptId = purchaseReturnReceipt.getPurchaseReturnOrder();
	//		for (Machine machine : machines) {
	//			machine.setPurchaseReturnReceiptId(purchaseReturnReceiptId);
	//			machine.setOperateEmpId(empId);
	//			machine.setStatusId(12);
	//		}
	//		if (machineService.updateBatchById(machines)) {
	//			logService.save(new Log(empId, "创建采购退货单", purchaseReturnReceiptMachine.toString(), LocalDateTime.now(), 0));
	//			return RespBean.success("添加成功");
	//		}
	//	}
	//	logService.save(new Log(empId, "创建采购退货单", purchaseReturnReceiptMachine.toString(), LocalDateTime.now(), 1));
	//	throw new RuntimeException("添加失败");
	//}

	@ApiOperation("创建采购退货单")
	@PutMapping("/")
	@Transactional
	public RespBean createPurchaseReturnReceipt(@RequestBody PurchaseReturnReceipt purchaseReturnReceipt, Authentication authentication) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			purchaseReturnReceipt.setCreateTime(now);
			purchaseReturnReceipt.setIsRelease(0);
			purchaseReturnReceipt.setOperateEmpId(empId);
			if (purchaseReturnReceiptService.save(purchaseReturnReceipt)) {
				logService.save(new Log(empId, "添加采购退货单", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 0));
				return RespBean.success("添加成功");
			}
			logService.save(new Log(empId, "添加采购退货单", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "添加采购退货单", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改采购退货单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody PurchaseReturnReceipt receipt, Authentication authentication) {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		if (!empId.equals(receipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该采购退货单已提交");
		}

		PurchaseReturnReceipt afterPurchaseReturnReceipt = new PurchaseReturnReceipt();
		afterPurchaseReturnReceipt.setComment(receipt.getComment());

		if (purchaseReturnReceiptService.update(afterPurchaseReturnReceipt, new UpdateWrapper<PurchaseReturnReceipt>().eq("purchase_return_order", receipt.getPurchaseReturnOrder()))) {
			logService.save(new Log(empId, "修改采购退货单", "采购退货单据id为" + receipt.getPurchaseReturnOrder() + "; 备注是：" + receipt.getComment(), now, 0));
			return RespBean.success("更新成功");
		}
		logService.save(new Log(empId, "修改采购退货单", "采购退货单据id为" + receipt.getPurchaseReturnOrder() + "; 备注是：" + receipt.getComment(), now, 1));
		return RespBean.error("更新失败");
	}


	@ApiOperation("删除采购退货单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		PurchaseReturnReceipt purchaseReturnReceipt = purchaseReturnReceiptService.getById(receiptId);
		try {
			if (!empId.equals(purchaseReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (purchaseReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该采购退货单已经提交了, 无法再删除");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("purchase_return_receipt_id", receiptId));
			List<MachineTrace> machineTraceList = new ArrayList<>();

			for (Machine machine : machines) {
				machine.setPurchaseReturnReceiptId(0);
				machine.setStatusId(machine.getPreviousStatusId());
				machineTraceList.add(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), -1, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
			}
			if (purchaseReturnReceiptService.removeById(receiptId)) {
				if (machines.size() == 0 || machineService.updateBatchById(machines)) {
					if (machineTraceService.saveBatch(machineTraceList)) {
						logService.save(new Log(empId, "删除采购退货单", "采购退货单据id为" + receiptId + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除采购退货单", "采购退货单据id为" + receiptId + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除采购退货单", "采购退货单据id为" + receiptId + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("往采购退货单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		PurchaseReturnReceipt purchaseReturnReceipt = purchaseReturnReceiptService.getById(receiptId);
		LocalDateTime now = LocalDateTime.now();
		try {
			if (!empId.equals(purchaseReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (purchaseReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该采购退货单已经发布了");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));
			List<MachineTrace> machineTraceList = new ArrayList<>();
			List<PurchaseReturnReceiptToMachine> purchaseReturnReceiptToMachines = new ArrayList<>();

			for (Machine machine : machines) {
				//机器状态判断
				//if (machine.getStatusId() != 1) {
				//	throw new RuntimeException("数据有误!");
				//}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(23);
				machine.setPurchaseReturnReceiptId(receiptId);
				machine.setOperateEmpId(empId);
				machineTraceList.add(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));

				purchaseReturnReceiptToMachines.add(new PurchaseReturnReceiptToMachine(null, receiptId, machine.getSku(), machine.getId(), machine.getSku(), machine.getPurchasePrice(), 23));
			}

			if (machineService.updateBatchById(machines)) {
				if (machineTraceService.saveBatch(machineTraceList)) {
					if (purchaseReturnReceiptToMachineService.saveBatch(purchaseReturnReceiptToMachines)) {
						logService.save(new Log(empId, "向采购退货单中添加机器", "采购退货单据id为" + receiptId + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 0));
						return RespBean.success("添加成功");
					}
				}
			}
			logService.save(new Log(empId, "向采购退货单中添加机器", "采购退货单据id为" + receiptId + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "向采购退货单中添加机器", "采购退货单据id为" + receiptId + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除采购退货单中的机器")
	@DeleteMapping("/deleteMachine")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(id);
		PurchaseReturnReceipt purchaseReturnReceipt = purchaseReturnReceiptService.getById(machine.getPurchaseReturnReceiptId());
		try {

			if (!empId.equals(purchaseReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			//if (purchaseReturnReceipt.getIsRelease() == 1) {
			//		return RespBean.error("采购退货单已经提交");
			//	}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setPurchaseReturnReceiptId(0);
			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), purchaseReturnReceipt.getPurchaseReturnOrder(), now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
					if (purchaseReturnReceiptToMachineService.remove(new QueryWrapper<PurchaseReturnReceiptToMachine>().eq("receipt_id", purchaseReturnReceipt.getPurchaseReturnOrder()).eq("machine_id", id))) {
						logService.save(new Log(empId, "删除采购退货单中的机器", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除采购退货单中的机器", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除采购退货单中的机器", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("发布采购退货单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseEnterStorageReceipt(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		PurchaseReturnReceipt purchaseReturnReceipt = purchaseReturnReceiptService.getById(receiptId);
		LocalDateTime now = LocalDateTime.now();
		try {
			if (!empId.equals(purchaseReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (purchaseReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该采购退货单已经发布了");
			}
			if (purchaseReturnReceiptService.update(new PurchaseReturnReceipt(), new UpdateWrapper<PurchaseReturnReceipt>().eq("purchase_return_order", receiptId).set("is_release", 1).set("release_time", now))) {


				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("purchase_return_receipt_id", receiptId).set("status_id", 3))) {
					List<Machine> machines = machineService.list(new UpdateWrapper<Machine>().eq("purchase_return_receipt_id", receiptId));
					List<MachineTrace> machineTraces = new ArrayList<>();
					for (Machine machine : machines) {
						machineTraces.add(new MachineTrace(machine.getId(), machine.getNumber(), 3, receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
					}
					if (machineTraceService.saveBatch(machineTraces)) {
						if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 1)) {
							logService.save(new Log(empId, "发布采购退货单据", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 0));
							return RespBean.success("发布成功");
						}
					}
				}
			}
			logService.save(new Log(empId, "发布采购退货单据", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "发布采购退货单据", "采购退货单据id为" + purchaseReturnReceipt.getPurchaseReturnOrder() + "; 备注是：" + purchaseReturnReceipt.getComment(), now, 1));
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("退货成功")
	@GetMapping("/returnSuccess")
	@Transactional
	public RespBean purchaseReturnSuccess(Integer machineId, Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(machineId);
		if (machine.getStatusId() != 3) {
			return RespBean.error("该机器不是退回中的机器");
		}
		if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("status_id", 24).set("operate_emp_id", empId).set("storage_location_id", null))) {
			if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), 24, -1, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
				if (purchaseReturnReceiptToMachineService.update(new PurchaseReturnReceiptToMachine(), new UpdateWrapper<PurchaseReturnReceiptToMachine>().eq("receipt_id", receiptId).eq("machine_id", machineId).set("status_id", 24))) {
					logService.save(new Log(empId, "退货成功", "机器number：" + machine.getNumber() + "; 备注是：" + machine.getComment(), now, 0));
					return RespBean.success("操作成功");
				}
			}
		}
		logService.save(new Log(empId, "退货成功", "机器number：" + machine.getNumber() + "; 备注是：" + machine.getComment(), now, 1));
		return RespBean.error("操作失败");
	}

	@ApiOperation("退货失败")
	@GetMapping("/returnError")
	@Transactional
	public RespBean purchaseReturnError(Integer machineId, Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(machineId);

		if (machine.getStatusId() != 3) {
			return RespBean.error("该机器不是退回中的机器");
		}
		if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("status_id", 4))) {
			if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), 4, -1, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
				if (purchaseReturnReceiptToMachineService.update(new PurchaseReturnReceiptToMachine(), new UpdateWrapper<PurchaseReturnReceiptToMachine>().eq("receipt_id", receiptId).eq("machine_id", machineId).set("status_id", 4))) {
					logService.save(new Log(empId, "退货失败", "机器number：" + machine.getNumber() + "; 备注是：" + machine.getComment(), now, 0));
					return RespBean.success("操作成功");
				}
			}
		}
		logService.save(new Log(empId, "退货失败", "机器number：" + machine.getNumber() + "; 备注是：" + machine.getComment(), now, 1));
		return RespBean.error("操作失败");
	}


}
