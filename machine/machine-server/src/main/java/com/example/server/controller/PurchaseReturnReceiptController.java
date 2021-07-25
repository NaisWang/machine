package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.server.pojo.*;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.PurchaseReturnReceiptServiceImpl;
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

	@ApiOperation("获取所有采购退货单据")
	@GetMapping("/")
	public RespBean getPurchaseReturnReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																					 @RequestParam(defaultValue = "10") Integer size,
																					 PurchaseReturnReceipt purchaseReturnReceipt) {
		RespPageBean respPageBean = purchaseReturnReceiptService.getPurchaseReturnReceipt(currentPage, size, purchaseReturnReceipt);
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
		try {
			LocalDate now = LocalDate.now();
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			purchaseReturnReceipt.setPurchaseReturnDate(now);
			purchaseReturnReceipt.setIsRelease(0);
			if (purchaseReturnReceiptService.save(purchaseReturnReceipt)) {
				logService.save(new Log(empId, "添加采购退货单", "", LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改采购退货单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody PurchaseReturnReceipt receipt) {
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该采购退货单已提交");
		}

		PurchaseReturnReceipt afterPurchaseReturnReceipt = new PurchaseReturnReceipt();
		afterPurchaseReturnReceipt.setComment(receipt.getComment());

		if (purchaseReturnReceiptService.update(afterPurchaseReturnReceipt, new UpdateWrapper<PurchaseReturnReceipt>().eq("purchase_return_order", receipt.getPurchaseReturnOrder()))) {
			return RespBean.success("更新成功");
		}
		return RespBean.error("更新失败");
	}


	@ApiOperation("删除采购退货单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId) {
		try {
			if (receiptId == 0) {
				return RespBean.error("数据错误");
			}
			PurchaseReturnReceipt purchaseReturnReceipt = purchaseReturnReceiptService.getById(receiptId);
			if (purchaseReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该采购退货单已经提交了, 无法再删除");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("purchase_return_receipt_id", receiptId));
			for (Machine machine : machines) {
				machine.setPurchaseReturnReceiptId(0);
				machine.setStatusId(machine.getPreviousStatusId());
			}
			if (purchaseReturnReceiptService.removeById(receiptId)) {
				if (machines.size() == 0 || machineService.updateBatchById(machines)) {
					return RespBean.success("删除成功");
				}
			}
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("往采购退货单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			PurchaseReturnReceipt purchaseReturnReceipt = purchaseReturnReceiptService.getById(receiptId);
			if (purchaseReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该采购退货单已经发布了");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));

			for (Machine machine : machines) {
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(23);
				machine.setPurchaseReturnReceiptId(receiptId);
				machine.setOperateEmpId(empId);
			}

			if (machineService.updateBatchById(machines)) {
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除销售退货单中的机器")
	@DeleteMapping("/deleteMachine")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();

			Machine machine = machineService.getById(id);

			if (purchaseReturnReceiptService.getById(machine.getPurchaseReturnReceiptId()).getIsRelease() == 1) {
				return RespBean.error("销售退货单已经提交");
			}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setPurchaseReturnReceiptId(0);
			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				return RespBean.success("删除成功");
			}

			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("发布采购退货单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseEnterStorageReceipt(Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			PurchaseReturnReceipt purchaseReturnReceipt = purchaseReturnReceiptService.getById(receiptId);
			if (purchaseReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该入库单已经发布了");
			}
			if (purchaseReturnReceiptService.update(new PurchaseReturnReceipt(), new UpdateWrapper<PurchaseReturnReceipt>().eq("purchase_return_order", receiptId).set("is_release", 1))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("purchase_return_receipt_id", receiptId).set("status_id", 3))) {
					logService.save(new Log(empId, "发布采购退货单", "采购退货单号为：" + receiptId, LocalDateTime.now(), 0));
					return RespBean.success("发布成功");
				}
			}
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("退货成功")
	@GetMapping("/returnSuccess")
	@Transactional
	public RespBean purchaseReturnSuccess(Integer machineId) {
		Machine machine = machineService.getById(machineId);
		if (machine.getStatusId() != 3) {
			return RespBean.error("该机器不是退回中的机器");
		}
		if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("status_id", 24))) {
			return RespBean.success("操作成功");
		}
		return RespBean.error("操作失败");
	}

	@ApiOperation("退货成功")
	@GetMapping("/returnError")
	@Transactional
	public RespBean purchaseReturnError(Integer machineId) {
		Machine machine = machineService.getById(machineId);
		if (machine.getStatusId() != 3) {
			return RespBean.error("该机器不是退回中的机器");
		}
		if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("status_id", 4))) {
			return RespBean.success("操作成功");
		}
		return RespBean.error("操作失败");
	}

}
