package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.server.pojo.*;
import com.example.server.service.ICategoryService;
import com.example.server.service.impl.*;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-22
 */
@RestController
@RequestMapping("/deliver/receipt")
public class DeliverReceiptController {

	@Autowired
	private DeliverReceiptServiceImpl deliverReceiptService;
	@Autowired
	private DeliverIntentionServiceImpl deliverIntentionService;
	@Autowired
	private DeliverMachineServiceImpl deliverMachineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private EmployeeServiceImpl employeeService;
	@Autowired
	private MachineServiceImpl machineService;

	@ApiOperation("分页获取转交单信息")
	@GetMapping("/")
	public RespBean getDeliverReceiptByPage(@RequestParam(defaultValue = "1") Integer currentPage,
																					@RequestParam(defaultValue = "10") Integer size,
																					DeliverReceipt deliverReceipt,
																					LocalDate[] localDateScope,
																					Integer type,
																					Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		deliverReceipt.setOperateEmpId(empId);
		IPage<DeliverReceipt> deliverReceiptIPage = null;
		System.out.println("aaa");
		System.out.println(type);
		System.out.println("aaaa");
		if (type == null || type == 0) {
			deliverReceiptIPage = deliverReceiptService.getDeliverReceiptByPage(currentPage, size, deliverReceipt, localDateScope, 0);
		} else {
			deliverReceiptIPage = deliverReceiptService.getDeliverReceiptByPage(currentPage, size, deliverReceipt, localDateScope, empId);
		}
		return RespBean.success("获取成功", new RespPageBean(deliverReceiptIPage.getTotal(), deliverReceiptIPage.getRecords()));
	}

	@ApiOperation("通过receiptId获取转交单")
	@GetMapping("/getByMachineId")
	public RespBean getDeliverReceiptById(Integer receiptId) {
		DeliverReceipt deliverReceiptList = deliverReceiptService.getById(receiptId);
		return RespBean.success("获取成功", deliverReceiptList);
	}


	@ApiOperation("通过deliverMachineId获取转交单")
	@GetMapping("/getByDeliverMachineId")
	public RespBean getDeliverReceiptByDeliverMachineId(Integer deliverMachineId) {
		DeliverMachine deliverMachine = deliverMachineService.getById(deliverMachineId);
		return RespBean.success("获取成功", deliverReceiptService.getById(deliverMachine.getDeliverReceiptId()));
	}


	@ApiOperation("添加转交单信息")
	@PutMapping("/")
	@Transactional
	public RespBean createDeliverReceipt(@RequestBody DeliverReceipt deliverReceipt, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			deliverReceipt.setDeliverDate(LocalDateTime.now());
			deliverReceipt.setSum(0);
			deliverReceipt.setNotReceiveSum(0);
			deliverReceipt.setEnableEdit(0);
			deliverReceipt.setOperateEmpId(empId);
			if (deliverReceiptService.save(deliverReceipt)) {
				StringBuffer receiveEmpsName = new StringBuffer();
				for (String id : deliverReceipt.getReceiveEmpIds().split(",")) {
					receiveEmpsName.append(employeeService.getById((Integer.parseInt(id))).getName() + "、");
				}
				logService.save(new Log(empId, "添加转交单", "转交类别为:" + deliverIntentionService.getById(deliverReceipt.getDeliverIntentionId()).getName() + "；指定人：" + receiveEmpsName, LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除转交单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteDeliverReceipt(Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			DeliverReceipt deliverReceipt = deliverReceiptService.getById(receiptId);
			if (deliverReceipt.getEnableEdit() == 1) {
				return RespBean.error("该转交单已提交，无法删除");
			}
			if (deliverReceiptService.removeById(receiptId)) {
				List<DeliverMachine> deliverMachines = deliverMachineService.list(new QueryWrapper<DeliverMachine>().eq("deliver_receipt_id", receiptId));

				for (DeliverMachine deliverMachine : deliverMachines) {
					if (!machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", deliverMachine.getMachineId()).set("deliver_receipt_id", 0))) {
						throw new RuntimeException("删除失败");
					}
				}

				if (deliverMachineService.remove(new QueryWrapper<DeliverMachine>().eq("deliver_receipt_id", receiptId))) {
					logService.save(new Log(empId, "删除转交单", "转交单号为:" + receiptId, LocalDateTime.now(), 0));
					return RespBean.success("删除成功");
				}
			}
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("发布转交单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseDeliverReceipt(Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			DeliverReceipt deliverReceipt = deliverReceiptService.getById(receiptId);
			if (deliverReceipt.getEnableEdit() == 1) {
				return RespBean.error("该转交单已经发布了");
			}
			if (deliverReceiptService.update(new DeliverReceipt(), new UpdateWrapper<DeliverReceipt>().eq("deliver_receipt_id", receiptId).set("enable_edit", 1))) {
				if (deliverMachineService.update(new DeliverMachine(), new UpdateWrapper<DeliverMachine>().eq("deliver_receipt_id", receiptId).set("status", 1))) {
					logService.save(new Log(empId, "发布转交单", "转交单号为：" + receiptId, LocalDateTime.now(), 0));
					return RespBean.success("发布成功");
				}
			}
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("获取需要接收的转接单")
	@GetMapping("/receive")
	public RespBean getReceiveDeliverReceipt(Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		List<DeliverReceipt> deliverReceiptList = deliverReceiptService.list(new QueryWrapper<DeliverReceipt>().likeRight("receive_emp_ids", empId + ",").or().likeLeft("receive_emp_ids", "," + empId).or().like("receive_emp_ids", "," + empId + ",").or().eq("receive_emp_ids", empId).orderByDesc("deliver_receipt_id"));
		System.out.println(deliverReceiptList);
		return RespBean.success("获取成功", deliverReceiptList);
	}

}
