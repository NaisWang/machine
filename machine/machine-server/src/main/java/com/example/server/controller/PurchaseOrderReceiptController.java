package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.PurchaseOrderReceiptServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-08
 */
@RestController
@RequestMapping("/machine/purchase/order")
public class PurchaseOrderReceiptController {

	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private PurchaseOrderReceiptServiceImpl purchaseOrderReceiptService;
	@Autowired
	private LogServiceImpl logService;


	@ApiOperation("获取采购单信息")
	@GetMapping("/")
	public RespBean getPurchaseOrderReceiptInfo(@RequestParam(defaultValue = "1") Integer currentPage,
																							@RequestParam(defaultValue = "10") Integer size,
																							PurchaseOrderReceipt purchaseOrder) {
		RespPageBean purchaseOrder1 = machineService.getPurchaseOrder(currentPage, size, purchaseOrder);
		return RespBean.success("获取成功", purchaseOrder1);
	}

	//@ApiOperation("创建采购单")
	//@PutMapping("/")
	//@Transactional
	//public RespBean createPurchaseOrderReceipt(@RequestBody Map<String, String> purchaseOrderReceiptMachine, Authentication authentication) throws Exception {
	//	System.out.println(purchaseOrderReceiptMachine);
	//	ObjectMapper objectMapper = new ObjectMapper();

	//	PurchaseOrderReceipt purchaseOrderReceipt = objectMapper.readValue((purchaseOrderReceiptMachine.get("purchaseOrderReceipt")), PurchaseOrderReceipt.class);
	//	List<Machine> machines = objectMapper.readValue(purchaseOrderReceiptMachine.get("machine"), new TypeReference<List<Machine>>() {
	//	});

	//	Integer empId = ((Employee) authentication.getPrincipal()).getId();

	//	LocalDateTime nowDateTime = LocalDateTime.now();
	//	LocalDate nowDate = LocalDate.now();
	//	purchaseOrderReceipt.setPurchaseDate(nowDate);
	//	purchaseOrderReceipt.setOperateEmpId(empId);

	//	if (purchaseOrderReceiptService.save(purchaseOrderReceipt)) {
	//		for (Machine machine : machines) {
	//			machine.setPurchaseOrderId(purchaseOrderReceipt.getPurchaseOrder());
	//			machine.setBiddingDate(nowDateTime);
	//			machine.setPurchaseEmpId(empId);
	//			machine.setOperateEmpId(empId);
	//			machine.setStatusId(1);
	//		}
	//		if (machineService.saveBatch(machines)) {
	//			logService.save(new Log(empId, "创建采购单", "采购单号为" + purchaseOrderReceipt.getPurchaseOrder(), LocalDateTime.now(), 0));
	//			return RespBean.success("添加成功");
	//		}
	//	}
	//	logService.save(new Log(empId, "创建采购单", "采购单号为" + purchaseOrderReceipt.getPurchaseOrder(), LocalDateTime.now(), 1));
	//	throw new RuntimeException("添加失败");
	//}


	@ApiOperation("创建采购单")
	@PutMapping("/")
	@Transactional
	public RespBean createPurchaseOrderReceipt(@RequestBody PurchaseOrderReceipt purchaseOrderReceipt, Authentication authentication) throws Exception {
		try {
			System.out.println("aaa");
			System.out.println(purchaseOrderReceipt);
			LocalDate now = LocalDate.now();
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			purchaseOrderReceipt.setPurchaseDate(now);
			purchaseOrderReceipt.setOperateEmpId(empId);
			purchaseOrderReceipt.setIsRelease(0);
			if (purchaseOrderReceiptService.save(purchaseOrderReceipt)) {
				logService.save(new Log(empId, "添加采购单", "", LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("发布采购单")
	@GetMapping("/release")
	@Transactional
	public RespBean releasePurchaseReceipt(Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			PurchaseOrderReceipt purchaseOrderReceipt = purchaseOrderReceiptService.getById(receiptId);
			if (purchaseOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该转交单已经发布了");
			}
			if (purchaseOrderReceiptService.update(new PurchaseOrderReceipt(), new UpdateWrapper<PurchaseOrderReceipt>().eq("purchase_order", receiptId).set("is_release", 1))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("purchase_order_id", receiptId).set("status_id", 1))) {
					logService.save(new Log(empId, "发布采购单", "采购单号为：" + receiptId, LocalDateTime.now(), 0));
					return RespBean.success("发布成功");
				}
			}
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("往采购单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Machine[] machines, Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			PurchaseOrderReceipt purchaseOrderReceipt = purchaseOrderReceiptService.getById(receiptId);
			if (purchaseOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该转交单已经发布了");
			}
			for (Machine machine : machines) {
				machine.setStatusId(21);
				machine.setPurchaseOrderId(receiptId);
				machine.setOperateEmpId(empId);
			}
			if (machineService.saveBatch(Arrays.asList(machines))) {
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("删除采购单中未提交的机器")
	@DeleteMapping("/deleteMachine")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		Machine machine = machineService.getById(id);
		if (machine.getStatusId() != 21) {
			return RespBean.error("不能删除该机器");
		}
		if (machineService.removeById(id)) {
			return RespBean.success("删除成功");
		}
		return RespBean.error("删除失败");
	}

	@ApiOperation("删除采购单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId) {
		try {
			PurchaseOrderReceipt purchaseOrderReceipt = purchaseOrderReceiptService.getById(receiptId);
			if (purchaseOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该采购单已经提交了, 无法再删除");
			}
			if (purchaseOrderReceiptService.removeById(receiptId)) {
				if (machineService.remove(new QueryWrapper<Machine>().eq("purchase_order_id", purchaseOrderReceipt.getPurchaseOrder()))) {
					return RespBean.success("删除成功");
				}
			}
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除失败");
		}
	}


	@ApiOperation("获取采购单最大订单号")
	@GetMapping("/getMaxId")
	public RespBean getPurchaseOrderReceiptMaxId() {
		return RespBean.success("获取成功", purchaseOrderReceiptService.getPurchaseOrderReceiptMaxId());
	}

	@ApiOperation("修改采购单中机器的信息")
	@PutMapping("/modifyMachine")
	public RespBean modifyMachine(@RequestBody Machine machine) {
		if (machine.getStatusId() != 21) {
			return RespBean.error("更新失败");
		}

		Machine afterMachine = new Machine();
		afterMachine.setId(machine.getId());
		afterMachine.setImei(machine.getImei());
		afterMachine.setCategoryId(machine.getCategoryId());
		afterMachine.setBrandId(machine.getBrandId());
		afterMachine.setType(machine.getType());
		afterMachine.setSku(machine.getSku());
		afterMachine.setRank(machine.getRank());
		afterMachine.setPurchasePrice(machine.getPurchasePrice());
		afterMachine.setDescribe(machine.getDescribe());
		afterMachine.setPurchaseEmpId(machine.getPurchaseEmpId());
		afterMachine.setComment(machine.getComment());

		if (machineService.update(afterMachine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
			return RespBean.success("更新成功");
		}
		return RespBean.error("更新失败");
	}

	@ApiOperation("修改采购单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody PurchaseOrderReceipt purchaseOrderReceipt) {
		if (purchaseOrderReceipt.getIsRelease() == 1) {
			return RespBean.error("该采购单已添加");
		}

		PurchaseOrderReceipt afterPurchaseOrderReceipt = new PurchaseOrderReceipt();
		afterPurchaseOrderReceipt.setPurchaseChannelId(purchaseOrderReceipt.getPurchaseChannelId());
		afterPurchaseOrderReceipt.setComment(purchaseOrderReceipt.getComment());

		if (purchaseOrderReceiptService.update(afterPurchaseOrderReceipt, new UpdateWrapper<PurchaseOrderReceipt>().eq("purchase_order", purchaseOrderReceipt.getPurchaseOrder()))) {
			return RespBean.success("更新成功");
		}
		return RespBean.error("更新失败");
	}

}
