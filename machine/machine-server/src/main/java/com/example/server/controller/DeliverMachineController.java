package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.JudgeCompleteDeliverIntention;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Update;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-22
 */
@RestController
@RequestMapping("/deliver/machine")
public class DeliverMachineController {

	@Autowired
	private DeliverMachineServiceImpl deliverMachineService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private DeliverReceiptServiceImpl deliverReceiptService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;


	@ApiOperation("分页获取转交单中的详情")
	@GetMapping("/")
	public RespBean getDeliverMachine(@RequestParam(defaultValue = "1") Integer currentPage,
																		@RequestParam(defaultValue = "10") Integer size,
																		DeliverMachine deliverMachine,
																		LocalDate[] localDateScope) {
		RespPageBean respPageBean = deliverMachineService.getDeliverMachine(currentPage, size, deliverMachine, localDateScope);
		return RespBean.success("获取成功", respPageBean);
	}

	//@ApiOperation("往转交单中添加机器")
	//@PutMapping("/")
	//@Transactional
	//public RespBean addDeliverMachine(@RequestBody DeliverMachine[] deliverMachines, Authentication authentication) {
	//	try {
	//		Integer empId = ((Employee) (authentication.getPrincipal())).getId();
	//		deliverMachineService.saveBatch(Arrays.asList(deliverMachines));
	//		Integer deliverReceiptId = deliverMachines[0].getDeliverReceiptId();
	//		Integer deliverMachineId = deliverMachines[0].getId();
	//		List<Integer> ids = new ArrayList<>();

	//		//判断是否可以添加
	//		for (DeliverMachine deliverMachine : deliverMachines) {
	//			Machine machine = machineService.getById(deliverMachine.getMachineId());
	//			if (machine.getDeliverMachineId() != 0) {
	//				throw new RuntimeException("添加失败, 机器id为" + machine.getId() + "的转交状态正在进行转交中");
	//			}
	//			ids.add(deliverMachine.getMachineId());
	//		}

	//		if (machineService.modifyMachineDeliverStatus(ids.toArray(new Integer[0]), deliverMachineId)) {
	//			logService.save(new Log(empId, "往转交单中添加机器", "转交单号为：" + deliverReceiptId + "。转交机器ids：" + ids.toString(), LocalDateTime.now(), 0));
	//			return RespBean.success("添加成功");
	//		}
	//		throw new RuntimeException("添加失败");
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//		throw new RuntimeException("添加失败");
	//	}
	//}

	@ApiOperation("往转交单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addDeliverMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) (authentication.getPrincipal())).getId();

			DeliverReceipt deliverReceipt = deliverReceiptService.getById(receiptId);
			if (deliverReceipt.getEnableEdit() == 1) {
				return RespBean.error("该转交单已经发布了");
			}

			if (!empId.equals(deliverReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			LocalDateTime now = LocalDateTime.now();

			List<DeliverMachine> deliverMachines = new ArrayList<>();
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", Arrays.asList(ids)));
			System.out.println(machines);
			List<Integer> idLists = new ArrayList<>();
			List<MachineTrace> machineTraces = new ArrayList<>();
			for (Machine machine : machines) {
				if (machine.getDeliverReceiptId() == null || machine.getDeliverReceiptId() == 0) {
					DeliverMachine deliverMachine = new DeliverMachine();
					deliverMachine.setDeliverReceiptId(receiptId);
					deliverMachine.setMachineId(machine.getId());
					deliverMachine.setStatus(0);
					deliverMachine.setReceiveDate(now);
					deliverMachine.setMachineNumber(machine.getNumber());
					deliverMachines.add(deliverMachine);

					MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf());
					machineTrace.setDeliverStatusId(0);
					machineTrace.setDeliverIntentionId(deliverReceipt.getDeliverIntentionId());
					machineTraces.add(machineTrace);

					idLists.add(machine.getId());
				}
			}
			if (deliverMachineService.saveBatch(deliverMachines)) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().in("id", idLists).set("deliver_receipt_id", receiptId))) {
					if (machineTraceService.saveBatch(machineTraces)) {
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

	@ApiOperation("删除转交单中的机器")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteDeliverMachine(String machineNumber, Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) (authentication.getPrincipal())).getId();
		LocalDateTime now = LocalDateTime.now();

		DeliverReceipt deliverReceipt = deliverReceiptService.getById(receiptId);
		if (!empId.equals(deliverReceipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}

		//if (deliverReceipt.getEnableEdit() == 1) {
		//	return RespBean.error("该转交单已经发布了");
		//}


		try {
			if (deliverMachineService.remove(new QueryWrapper<DeliverMachine>().eq("machine_number", machineNumber).eq("deliver_receipt_id", receiptId))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("number", machineNumber).set("deliver_receipt_id", 0))) {
					Machine machine = machineService.getOne(new QueryWrapper<Machine>().eq("number", machineNumber));
					MachineTrace machineTrace = new MachineTrace(machine.getId(),machineNumber, machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf());
					if (machineTraceService.save(machineTrace)) {
						return RespBean.success("删除成功");
					}
				}
			}
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("获取机器的转交状态")
	@GetMapping("/status")
	public RespBean getDeliverStatus(Integer machineId, Integer deliverReceiptId) {
		Map<String, Integer> queryMap = new HashMap<>();
		queryMap.put("machine_id", machineId);
		queryMap.put("deliver_receipt_id", deliverReceiptId);
		List<DeliverMachine> deliverMachines = deliverMachineService.list(new QueryWrapper<DeliverMachine>().allEq(queryMap));
		if (deliverMachines.size() == 1) {
			return RespBean.success("获取成功", deliverMachines.get(0).getStatus());
		}
		return RespBean.error("获取失败");
	}

	@ApiOperation("接收机器")
	@GetMapping("/receive")
	@Transactional
	public RespBean receiveMachine(Integer machineId, Authentication authentication) {
		try {
			Employee employee = (Employee) authentication.getPrincipal();
			Integer empId = employee.getId();

			Machine machine = machineService.getById(machineId);
			Integer deliverReceiptId = machine.getDeliverReceiptId();
			DeliverReceipt deliverReceipt = deliverReceiptService.getById(deliverReceiptId);
			if (!Arrays.asList(deliverReceipt.getReceiveEmpIds().split(",")).contains(empId + "")) {
				return RespBean.error("你不能接收该机器");
			}
			if (deliverReceiptId == null || deliverReceiptId == 0) {
				return RespBean.error("该机器已经接收");
			}

			Map<String, Integer> queryMap = new HashMap<>();
			queryMap.put("machine_id", machineId);
			queryMap.put("deliver_receipt_id", deliverReceiptId);

			LocalDateTime now = LocalDateTime.now();

			DeliverMachine deliverMachine = deliverMachineService.getOne(new QueryWrapper<DeliverMachine>().allEq(queryMap));
			deliverMachine.setReceiveEmpId(empId);
			deliverMachine.setStatus(2);
			deliverMachine.setReceiveDate(now);

			if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("deliver_receipt_id", 0).set("operate_emp_id", empId))) {
				if (deliverMachineService.update(deliverMachine, new QueryWrapper<DeliverMachine>().allEq(queryMap))) {
					MachineTrace machineTrace = new MachineTrace(machineId, machine.getNumber(), machine.getStatusId(), deliverMachine.getDeliverReceiptId(), now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf());
					machineTrace.setDeliverStatusId(2);
					machineTrace.setDeliverIntentionId(deliverReceipt.getDeliverIntentionId());
					if (machineTraceService.save(machineTrace)) {
						List<Machine> machines = new ArrayList<>();
						machines.add(machine);
						if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 11)) {
							logService.save(new Log(empId, "接收机器", "机器id为" + machineId, now, 0));
							return RespBean.success("接收成功");
						}
					}
				}
			}
			throw new RuntimeException("接收失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("接收失败");
		}
	}
}
