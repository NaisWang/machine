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
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.apache.ibatis.annotations.Update;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
@RestController
@RequestMapping("/machine/fix/send/receipt")
public class SendFixReceiptController {

	@Autowired
	private SendFixMachineServiceImpl sendFixMachineService;
	@Autowired
	private SendFixReceiptServiceImpl sendFixReceiptService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@ApiModelProperty("获取所有外修单")
	@GetMapping("/")
	public RespBean getSendFixReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																		@RequestParam(defaultValue = "10") Integer size,
																		SendFixReceipt sendFixReceipt) {
		RespPageBean respPageBean = sendFixReceiptService.getSendFixReceipt(currentPage, size, sendFixReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiOperation("创建外修单")
	@PutMapping("/")
	@Transactional
	public RespBean createEnterStorageReceipt(@RequestBody SendFixReceipt sendFixReceipt, Authentication authentication) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			sendFixReceipt.setCreateTime(now);
			sendFixReceipt.setIsRelease(0);
			sendFixReceipt.setOperateEmpId(empId);

			if (sendFixReceiptService.save(sendFixReceipt)) {
				logService.save(new Log(empId, "添加外修单", "单号为：" + sendFixReceipt.getId(), now, 0));
				return RespBean.success("添加成功");
			}
			logService.save(new Log(empId, "添加外修单", "", now, 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "添加外修单", "", now, 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改送外修单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody SendFixReceipt receipt, Authentication authentication) {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		if (!empId.equals(receipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该送外修单已发布");
		}

		SendFixReceipt afterSendFixReceipt = new SendFixReceipt();
		afterSendFixReceipt.setComment(receipt.getComment());

		if (sendFixReceiptService.update(afterSendFixReceipt, new UpdateWrapper<SendFixReceipt>().eq("id", receipt.getId()))) {
			logService.save(new Log(empId, "修改外修单的信息", "单号为：" + receipt.getId(), now, 0));
			return RespBean.success("更新成功");
		}
		return RespBean.error("更新失败");
	}

	@ApiOperation("删除送外维修单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		try {
			SendFixReceipt sendFixReceipt = sendFixReceiptService.getById(receiptId);
			if (!empId.equals(sendFixReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (sendFixReceipt.getIsRelease() == 1) {
				return RespBean.error("该送外修单已发布");
			}
			List<SendFixMachine> sendFixMachines = sendFixMachineService.list(new QueryWrapper<SendFixMachine>().eq("receipt_id", receiptId));
			List<String> ids = new ArrayList<>();
			for (SendFixMachine sendFixMachine : sendFixMachines) {
				ids.add(sendFixMachine.getNumber());
			}
			if (ids.size() != 0) {
				List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("number", ids));
				List<MachineTrace> machineTraces = new ArrayList<>();
				for (Machine machine : machines) {
					machine.setStatusId(machine.getPreviousStatusId());
					machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId()));
				}
				if (machineService.updateBatchById(machines)) {
					if (!machineTraceService.saveBatch(machineTraces)) {
						throw new RuntimeException("删除失败");
					}
				} else {
					throw new RuntimeException("删除失败");
				}
			}
			if (sendFixMachineService.remove(new QueryWrapper<SendFixMachine>().eq("receipt_id", receiptId))) {
				if (sendFixReceiptService.removeById(receiptId)) {
					logService.save(new Log(empId, "删除外修单的信息", "单号为：" + receiptId, now, 0));
					return RespBean.success("删除成功");
				}
			}
			logService.save(new Log(empId, "删除外修单的信息", "单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除外修单的信息", "单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("提交外维修单")
	@GetMapping("/release")
	public RespBean releaseReceipt(Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		try {
			SendFixReceipt sendFixReceipt = sendFixReceiptService.getById(receiptId);
			if (!empId.equals(sendFixReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (sendFixReceipt.getIsRelease() == 1) {
				return RespBean.error("该送外修单已发布");
			}

			List<SendFixMachine> sendFixMachines = sendFixMachineService.list(new QueryWrapper<SendFixMachine>().eq("receipt_id", receiptId));
			if (sendFixMachines.size() == 0) {
				return RespBean.error("该送外修单没有机器，不能发布");
			}

			//修改外修单为已发布
			if (!sendFixReceiptService.update(new SendFixReceipt(), new UpdateWrapper<SendFixReceipt>().eq("id", receiptId).set("is_release", 1).set("release_time", LocalDateTime.now()))) {
				return RespBean.error("送修失败");
			}

			//修改外维修单中的机器状态
			if (!sendFixMachineService.update(new SendFixMachine(), new UpdateWrapper<SendFixMachine>().eq("receipt_id", receiptId).set("fix_status", 1))) {
				throw new RuntimeException("发布失败");
			}

			//修改外维修单中的机器状态
			List<String> ids = new ArrayList<>();
			for (SendFixMachine sendFixMachine : sendFixMachines) {
				ids.add(sendFixMachine.getNumber());
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("number", ids));
			List<MachineTrace> machineTraces = new ArrayList<>();
			for (Machine machine : machines) {
				machine.setStatusId(17);
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId()));
			}
			if (machineService.updateBatchById(machines)) {
				if (machineTraceService.saveBatch(machineTraces)) {
					logService.save(new Log(empId, "发布外修单的信息", "单号为：" + receiptId, now, 0));
					return RespBean.success("发布成功");
				}
			}
			logService.save(new Log(empId, "发布外修单的信息", "单号为：" + receiptId, now, 1));
			throw new RuntimeException("提交失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "发布外修单的信息", "单号为：" + receiptId, now, 1));
			throw new RuntimeException("提交失败");
		}
	}

}
