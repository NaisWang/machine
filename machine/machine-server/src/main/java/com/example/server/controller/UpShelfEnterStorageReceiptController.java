package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.MachineTraceServiceImpl;
import com.example.server.service.impl.UpShelfEnterStorageReceiptServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
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

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
@RestController
@RequestMapping("/up-shelf/enter-storage")
public class UpShelfEnterStorageReceiptController {

	@Autowired
	private UpShelfEnterStorageReceiptServiceImpl upShelfEnterStorageReceiptService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@ApiModelProperty("获取所有上架入库单据")
	@GetMapping("/")
	public RespBean getUpShelfEnterStorageReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																								@RequestParam(defaultValue = "10") Integer size,
																								UpShelfEnterStorageReceipt upShelfEnterStorageReceipt) {
		System.out.println(upShelfEnterStorageReceipt);
		RespPageBean respPageBean = upShelfEnterStorageReceiptService.getUpShelfEnterStorageReceipt(currentPage, size, upShelfEnterStorageReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiOperation("创建上架入库单")
	@PutMapping("/")
	@Transactional
	public RespBean createUpShelfEnterStorageReceipt(@RequestBody UpShelfEnterStorageReceipt upShelfEnterStorageReceipt, Authentication authentication) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			upShelfEnterStorageReceipt.setCreateTime(now);
			upShelfEnterStorageReceipt.setIsRelease(0);
			upShelfEnterStorageReceipt.setOperateEmpId(empId);
			if (upShelfEnterStorageReceiptService.save(upShelfEnterStorageReceipt)) {
				logService.save(new Log(empId, "添加入上架入库单", "", LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			logService.save(new Log(empId, "添加入上架入库单", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "添加入上架入库单", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改上架入库单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody UpShelfEnterStorageReceipt receipt, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		if (!empId.equals(receipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该上架入库单已添加");
		}

		UpShelfEnterStorageReceipt afterUpShelfEnterStorageReceipt = new UpShelfEnterStorageReceipt();
		afterUpShelfEnterStorageReceipt.setComment(receipt.getComment());

		if (upShelfEnterStorageReceiptService.update(afterUpShelfEnterStorageReceipt, new UpdateWrapper<UpShelfEnterStorageReceipt>().eq("id", receipt.getId()))) {
			logService.save(new Log(empId, "修改上架入库单", "", LocalDateTime.now(), 0));
			return RespBean.success("更新成功");
		}
		logService.save(new Log(empId, "修改上架入库单", "", LocalDateTime.now(), 1));
		return RespBean.error("更新失败");
	}

	@ApiOperation("往上架入库单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			UpShelfEnterStorageReceipt upShelfEnterStorageReceipt = upShelfEnterStorageReceiptService.getById(receiptId);
			if (!empId.equals(upShelfEnterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (upShelfEnterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该销退入库单已经发布了");
			}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));
			List<MachineTrace> machineTraces = new ArrayList<>();

			for (Machine machine : machines) {
				//状态判断
				if (machine.getStatusId() != 11) {
					return RespBean.error("数据有误！！！");
				}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(34);
				machine.setUpShelfEnterStorageId(receiptId);
				machine.setOperateEmpId(empId);
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId()));
			}

			if (machineService.updateBatchById(machines)) {
				if (machineTraceService.saveBatch(machineTraces)) {
					logService.save(new Log(empId, "往上架入库单中添加机器", "", LocalDateTime.now(), 0));
					return RespBean.success("添加成功");
				}
			}
			logService.save(new Log(empId, "往上架入库单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "往上架入库单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除上架入库单中的机器")
	@DeleteMapping("/deleteMachine/")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(id);
		try {
			UpShelfEnterStorageReceipt upShelfEnterStorageReceipt = upShelfEnterStorageReceiptService.getById(machine.getUpShelfEnterStorageId());
			if (!empId.equals(upShelfEnterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (upShelfEnterStorageReceiptService.getById(machine.getUpShelfEnterStorageId()).getIsRelease() == 1) {
				return RespBean.error("销退入库单已经提交");
			}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setUpShelfEnterStorageId(0);

			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineTraceService.save(new MachineTrace(machine.getNumber(), machine.getStatusId(), -1, now, empId, machine.getComment(), machine.getStorageLocationId()))) {
					logService.save(new Log(empId, "删除上架入库单中机器", "", LocalDateTime.now(), 0));
					return RespBean.success("删除成功");
				}
			}
			logService.save(new Log(empId, "删除上架入库单中机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除上架入库单中机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("发布上架入库单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseUpShelfEnterStorage(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		UpShelfEnterStorageReceipt upShelfEnterStorageReceipt = upShelfEnterStorageReceiptService.getById(receiptId);
		try {
			if (!empId.equals(upShelfEnterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (upShelfEnterStorageReceipt.getIsRelease() == 1) {
				return RespBean.error("该上架入库单已经发布了");
			}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("up_shelf_enter_storage_id", receiptId));
			if (machines.size() == 0) {
				return RespBean.error("该发布入库单为空，不能发布");
			}

			if (upShelfEnterStorageReceiptService.update(new UpShelfEnterStorageReceipt(), new UpdateWrapper<UpShelfEnterStorageReceipt>().eq("id", receiptId).set("is_release", 1).set("release_time", now))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("up_shelf_enter_storage_id", receiptId).set("status_id", 26).set("storage_location_id", upShelfEnterStorageReceipt.getStorageLocationId()))) {
					List<MachineTrace> machineTraces = new ArrayList<>();
					for (Machine machine : machines) {
						MachineTrace machineTrace = new MachineTrace(machine.getNumber(), 26, receiptId, now, empId, machine.getComment(), upShelfEnterStorageReceipt.getStorageLocationId());
						machineTrace.setStorageLocationId(upShelfEnterStorageReceipt.getStorageLocationId());
						machineTraces.add(machineTrace);
					}
					if (machineTraceService.saveBatch(machineTraces)) {
						logService.save(new Log(empId, "发布上架入库单", "上架入库单号为：" + receiptId, now, 0));
						return RespBean.success("发布成功");
					}
				}
			}
			logService.save(new Log(empId, "发布上架入库单", "上架入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "发布上架入库单", "上架入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("删除上架入库单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		UpShelfEnterStorageReceipt upShelfEnterStorageReceipt = upShelfEnterStorageReceiptService.getById(receiptId);
		try {
			if (!empId.equals(upShelfEnterStorageReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			//if (upShelfEnterStorageReceipt.getIsRelease() == 1) {
			//		return RespBean.error("该上架入库单已经提交了, 无法再删除");
			//	}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("up_shelf_enter_storage_id", receiptId));
			List<MachineTrace> machineTraces = new ArrayList<>();

			for (Machine machine : machines) {
				machine.setUpShelfEnterStorageId(0);
				machine.setStatusId(machine.getPreviousStatusId());
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId()));
			}
			if (upShelfEnterStorageReceiptService.removeById(receiptId)) {
				if (machines.size() == 0 || machineService.updateBatchById(machines)) {
					if (machineTraceService.saveBatch(machineTraces)) {
						logService.save(new Log(empId, "删除上架入库单", "上架入库单号为：" + receiptId, now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除上架入库单", "上架入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除上架入库单", "上架入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		}
	}

}
