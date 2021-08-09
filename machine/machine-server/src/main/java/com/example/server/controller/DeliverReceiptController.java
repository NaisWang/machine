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
import java.util.ArrayList;
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
	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@ApiOperation("分页获取转交单信息")
	@GetMapping("/")
	public RespBean getDeliverReceiptByPage(@RequestParam(defaultValue = "1") Integer currentPage,
																					@RequestParam(defaultValue = "10") Integer size,
																					DeliverReceipt deliverReceipt,
																					LocalDate[] localDateScope,
																					Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		deliverReceipt.setOperateEmpId(empId);
		IPage<DeliverReceipt> deliverReceiptIPage = deliverReceiptService.getDeliverReceiptByPage(currentPage, size, deliverReceipt, localDateScope);
		return RespBean.success("获取成功", new RespPageBean(deliverReceiptIPage.getTotal(), deliverReceiptIPage.getRecords()));
	}

	@ApiOperation("获取需要接收的转接单")
	@GetMapping("/receive")
	public RespBean getReceiveDeliverReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																					 @RequestParam(defaultValue = "10") Integer size,
																					 DeliverReceipt deliverReceipt,
																					 LocalDate[] localDateScope,
																					 Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		RespPageBean respPageBean = deliverReceiptService.getReceiveDeliverReceipt(currentPage, size, deliverReceipt, empId);
		//List<DeliverReceipt> deliverReceiptList = deliverReceiptService.list(new QueryWrapper<DeliverReceipt>().likeRight("receive_emp_ids", empId + ",").or().likeLeft("receive_emp_ids", "," + empId).or().like("receive_emp_ids", "," + empId + ",").or().eq("receive_emp_ids", empId).orderByDesc("deliver_receipt_id"));
		return RespBean.success("获取成功", respPageBean);
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
			LocalDateTime now = LocalDateTime.now();
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			deliverReceipt.setCreateTime(now);
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

			if (!empId.equals(deliverReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			//if (deliverReceipt.getIsDelete() == 1) {
			//		return RespBean.error("该转交单已提交，无法删除");
			//	}

			if (deliverReceiptService.removeById(receiptId)) {
				List<DeliverMachine> deliverMachines = deliverMachineService.list(new QueryWrapper<DeliverMachine>().eq("deliver_receipt_id", receiptId));

				for (DeliverMachine deliverMachine : deliverMachines) {
					if (!machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", deliverMachine.getMachineId()).set("deliver_receipt_id", 0).set("need_complete_deliver_receipt_id", 0))) {
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
			LocalDateTime now = LocalDateTime.now();
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			DeliverReceipt deliverReceipt = deliverReceiptService.getById(receiptId);

			if (!empId.equals(deliverReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (deliverReceipt.getEnableEdit() == 1) {
				return RespBean.error("该转交单已经发布了");
			}

			List<DeliverMachine> deliverMachines = deliverMachineService.list(new QueryWrapper<DeliverMachine>().eq("deliver_receipt_id", receiptId));
			List<MachineTrace> machineTraces = new ArrayList<>();
			if (deliverMachines.size() == 0) {
				return RespBean.error("该转交为空，不能提交");
			}

			List<String> machineNumbers = new ArrayList<>();

			for (DeliverMachine deliverMachine : deliverMachines) {
				machineNumbers.add(deliverMachine.getMachineNumber());
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("number", machineNumbers));
			for (Machine machine : machines) {
				MachineTrace machineTrace = new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf());

				machine.setNeedCompleteDeliverReceiptId(receiptId);

				//如果为“成色检测（不上架）”类别转交单，则设置为不上架
				if (deliverReceipt.getDeliverIntentionId() == 12) {
					machine.setIsUpShelf(1);
					machineTrace.setIsUpShelf(1);
				}

				machineTrace.setDeliverStatusId(1);
				machineTrace.setDeliverIntentionId(deliverReceipt.getDeliverIntentionId());
				machineTraces.add(machineTrace);
			}

			if (deliverReceiptService.update(new DeliverReceipt(), new UpdateWrapper<DeliverReceipt>().eq("deliver_receipt_id", receiptId).set("enable_edit", 1).set("release_time", now))) {
				if (deliverMachineService.update(new DeliverMachine(), new UpdateWrapper<DeliverMachine>().eq("deliver_receipt_id", receiptId).set("status", 1))) {
					if (machineService.updateBatchById(machines)) {
						if (machineTraceService.saveBatch(machineTraces)) {
							logService.save(new Log(empId, "发布转交单", "转交单号为：" + receiptId, now, 0));
							return RespBean.success("发布成功");
						}
					}
				}
			}
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("发布失败");
		}
	}

}
