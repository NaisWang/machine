package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
@RestController
@RequestMapping("/machine/fix/send/machine")
public class SendFixMachineController {

	@Autowired
	private SendFixMachineServiceImpl sendFixMachineService;
	@Autowired
	private SendFixReceiptServiceImpl sendFixReceiptService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;
	@Autowired
	private LogServiceImpl logService;

	@ApiOperation("分页获取转交单中的详情")
	@GetMapping("/")
	public RespBean getSendFixMachine(@RequestParam(defaultValue = "1") Integer currentPage,
																		@RequestParam(defaultValue = "10") Integer size,
																		SendFixMachine sendFixMachine,
																		LocalDate[] localDateScope) {
		RespPageBean respPageBean = sendFixMachineService.getSendFixMachine(currentPage, size, sendFixMachine, localDateScope);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiOperation("往外修单中添加机器")
	@PutMapping("/")
	@Transactional
	public RespBean addMachine(@RequestBody SendFixMachine sendFixMachine, Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		try {
			SendFixReceipt sendFixReceipt = sendFixReceiptService.getById(receiptId);
			if (!empId.equals(sendFixReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (sendFixReceipt.getIsRelease() == 1) {
				return RespBean.error("该外修单已经提交了");
			}

			sendFixMachine.setSendFixDate(now);
			sendFixMachine.setReceiptId(receiptId);
			sendFixMachine.setFixStatus(0);

			Machine machine = machineService.getOne(new QueryWrapper<Machine>().eq("number", sendFixMachine.getNumber()));
			//状态判断
			Integer[] legalStatus = {32, 31};
			if (!Arrays.asList(legalStatus).contains(machine.getStatusId())) {
				return RespBean.error("数据有误");
			}

			machine.setPreviousStatusId(machine.getStatusId());
			machine.setStatusId(30);

			if (sendFixMachineService.save(sendFixMachine)) {
				if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
					if (machineTraceService.save(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
						logService.save(new Log(empId, "往外修单中添加机器", "外修单据id为" + receiptId, now, 0));
						return RespBean.success("添加成功");
					}
				}
			}
			logService.save(new Log(empId, "往外修单中添加机器", "外修单据id为" + receiptId, now, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "往外修单中添加机器", "外修单据id为" + receiptId, now, 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除送外修中的机器")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		SendFixMachine sendFixMachine = sendFixMachineService.getById(id);
		Integer receiptId = sendFixMachine.getReceiptId();
		try {
			SendFixReceipt sendFixReceipt = sendFixReceiptService.getById(receiptId);
			if (!empId.equals(sendFixReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (sendFixReceipt.getIsRelease() == 1) {
				return RespBean.error("该外修单已经提交了");
			}

			if (sendFixMachineService.removeById(id)) {
				String number = sendFixMachine.getNumber();
				Machine machine = machineService.getOne(new QueryWrapper<Machine>().eq("number", number));
				machine.setStatusId(machine.getPreviousStatusId());
				if (machineService.update(machine, new UpdateWrapper<Machine>().eq("number", number))) {
					if (machineTraceService.save(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
						logService.save(new Log(empId, "删除外修单中机器", "外修单据id为" + receiptId, now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除外修单中机器", "外修单据id为" + receiptId, now, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除外修单中机器", "外修单据id为" + receiptId, now, 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("机器取回")
	@GetMapping("/receiveMachine")
	@Transactional
	public RespBean receiveMachine(String number, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		try {
			Machine machine = machineService.getOne(new QueryWrapper<Machine>().eq("number", number));
			if (machine == null) {
				return RespBean.error("没有该物品编号");
			}
			if (machine.getStatusId() != 17) {
				return RespBean.error("该机器不是送外修机器");
			}
			machine.setStatusId(31);
			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				SendFixMachine sendFixMachine = sendFixMachineService.getOne(new QueryWrapper<SendFixMachine>().orderByDesc("send_fix_date").eq("number", number));
				if (sendFixMachineService.update(new SendFixMachine(), new UpdateWrapper<SendFixMachine>().eq("send_fix_machine_id", sendFixMachine.getSendFixMachineId()).set("fix_status", 2).set("receive_time", LocalDateTime.now()))) {
					if (machineTraceService.save(new MachineTrace(machine.getNumber(), machine.getStatusId(), sendFixMachine.getReceiptId(), now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
						logService.save(new Log(empId, "机器取回", "机器number：" + number, now, 0));
						return RespBean.success("取回成功");
					}
				}
			}
			logService.save(new Log(empId, "机器取回", "机器number：" + number, now, 1));
			throw new RuntimeException("取回失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "机器取回", "机器number：" + number, now, 1));
			throw new RuntimeException("取回失败");
		}
	}

}
