package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.DeliverMachineServiceImpl;
import com.example.server.service.impl.DeliverReceiptServiceImpl;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Update;
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

			LocalDateTime now = LocalDateTime.now();

			List<DeliverMachine> deliverMachines = new ArrayList<>();
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", Arrays.asList(ids)));
			System.out.println(machines);
			List<Integer> idLists = new ArrayList<>();
			for (Machine machine : machines) {
				if (machine.getDeliverReceiptId() == null || machine.getDeliverReceiptId() == 0) {
					DeliverMachine deliverMachine = new DeliverMachine();
					deliverMachine.setDeliverReceiptId(receiptId);
					deliverMachine.setMachineId(machine.getId());
					deliverMachine.setStatus(0);
					deliverMachine.setReceiveDate(now);
					deliverMachine.setMachineNumber(machine.getNumber());
					deliverMachines.add(deliverMachine);
					idLists.add(machine.getId());
				}
			}
			if (deliverMachineService.saveBatch(deliverMachines)) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().in("id", idLists).set("deliver_receipt_id", receiptId))) {
					return RespBean.success("添加成功");
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
	public RespBean deleteDeliverMachine(Integer machineId, Integer receiptId) {
		if (deliverReceiptService.getById(receiptId).getEnableEdit() == 1) {
			return RespBean.error("该单据不能再进行编辑");
		}
		try {
			Map<String, Integer> queryMap = new HashMap<>();
			queryMap.put("machine_id", machineId);
			queryMap.put("deliver_receipt_id", receiptId);
			if (deliverMachineService.remove(new QueryWrapper<DeliverMachine>().allEq(queryMap))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("deliver_receipt_id", 0))) {
					return RespBean.success("删除成功");
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
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
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

			if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("deliver_receipt_id", 0))) {
				if (deliverMachineService.update(deliverMachine, new QueryWrapper<DeliverMachine>().allEq(queryMap))) {
					logService.save(new Log(empId, "接收机器", "机器id为" + machineId, now, 0));
					return RespBean.success("接收成功");
				}
			}
			throw new RuntimeException("接收失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("接收失败");
		}
	}
}
