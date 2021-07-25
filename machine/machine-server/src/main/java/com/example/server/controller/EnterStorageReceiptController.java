package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.EnterStorageReceiptServiceImpl;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.LoginServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.springframework.aop.interceptor.ExposeBeanNameAdvisors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
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

	@ApiModelProperty("获取所有入库单据")
	@GetMapping("/")
	public RespBean getEnterStorageReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																				 @RequestParam(defaultValue = "10") Integer size,
																				 EnterStorageReceipt enterStorageReceipt) {
		RespPageBean respPageBean = enterStorageReceiptService.getEnterStorageReceipt(currentPage, size, enterStorageReceipt);
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
		try {
			System.out.println("aaa");
			System.out.println(enterStorageReceipt);
			LocalDate now = LocalDate.now();
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			enterStorageReceipt.setEnterStorageDate(now);
			enterStorageReceipt.setIsRelease(0);
			if (enterStorageReceiptService.save(enterStorageReceipt)) {
				logService.save(new Log(empId, "添加入库单", "", LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("发布入库单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseEnterStorageReceipt(Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			EnterStorageReceipt enterStorageReceipt = enterStorageReceiptService.getById(receiptId);
			if (enterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该入库单已经发布了");
			}
			if (enterStorageReceiptService.update(new EnterStorageReceipt(), new UpdateWrapper<EnterStorageReceipt>().eq("enter_storage_order", receiptId).set("is_release", 1))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("enter_storage_receipt_id", receiptId).set("status_id", 2))) {
					logService.save(new Log(empId, "发布入库单", "入库单号为：" + receiptId, LocalDateTime.now(), 0));
					return RespBean.success("发布成功");
				}
			}
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("往入库单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			EnterStorageReceipt enterStorageReceipt = enterStorageReceiptService.getById(receiptId);
			if (enterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该入库单已经发布了");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));

			for (Machine machine : machines) {
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(20);
				machine.setEnterStorageReceiptId(receiptId);
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

	@ApiOperation("删除入库单中的机器")
	@DeleteMapping("/deleteMachine/")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();

			Machine machine = machineService.getById(id);

			if (enterStorageReceiptService.getById(machine.getEnterStorageReceiptId()).getIsRelease() == 1) {
				return RespBean.error("入库单已经提交");
			}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setEnterStorageReceiptId(0);
			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				return RespBean.success("删除成功");
			}

			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除失败");
		}
	}


	@ApiModelProperty("一键入库")
	@PutMapping("/one-key-enter/")
	@Transactional
	public RespBean createEnterStorageReceiptByOneKey(@RequestBody Map<String, String> enterStorageReceiptMachine, Authentication authentication) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		Integer empId = ((Employee) authentication.getPrincipal()).getId();

		EnterStorageReceipt enterStorageReceipt = objectMapper.readValue(enterStorageReceiptMachine.get("enterStorageReceipt"), EnterStorageReceipt.class);
		Integer purchaseOrder = Integer.parseInt(enterStorageReceiptMachine.get("purchaseOrder"));

		System.out.println("+++" + purchaseOrder);

		LocalDateTime nowDateTime = LocalDateTime.now();
		LocalDate nowDate = LocalDate.now();
		enterStorageReceipt.setEnterStorageDate(nowDate);

		if (enterStorageReceiptService.save(enterStorageReceipt)) {
			if (machineService.updateEnterStorageReceiptForOneKey(enterStorageReceipt, purchaseOrder, nowDateTime, empId)) {
				logService.save(new Log(empId, "一键入库", "采购单号为" + purchaseOrder, nowDateTime, 0));
				return RespBean.success("添加成功");
			}
		}
		logService.save(new Log(empId, "一键入库", "采购单号为" + purchaseOrder, nowDateTime, 1));
		throw new RuntimeException("添加失败");
	}

	@ApiOperation("修改入库单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody EnterStorageReceipt receipt) {
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该采购单已添加");
		}

		EnterStorageReceipt afterEnterStorageReceipt = new EnterStorageReceipt();
		afterEnterStorageReceipt.setComment(receipt.getComment());

		if (enterStorageReceiptService.update(afterEnterStorageReceipt, new UpdateWrapper<EnterStorageReceipt>().eq("enter_storage_order", receipt.getEnterStorageOrder()))) {
			return RespBean.success("更新成功");
		}
		return RespBean.error("更新失败");
	}

	@ApiOperation("删除入库单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId) {
		try {
			if (receiptId == 0) {
				return RespBean.error("数据错误");
			}
			EnterStorageReceipt enterStorageReceipt = enterStorageReceiptService.getById(receiptId);
			if (enterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该采购单已经提交了, 无法再删除");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("enter_storage_receipt_id", receiptId));
			for (Machine machine : machines) {
				machine.setPurchaseReturnReceiptId(0);
				machine.setStatusId(machine.getPreviousStatusId());
			}
			if (enterStorageReceiptService.removeById(receiptId)) {
				if (machineService.updateBatchById(machines)) {
					return RespBean.success("删除成功");
				}
			}
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除失败");
		}
	}

}
