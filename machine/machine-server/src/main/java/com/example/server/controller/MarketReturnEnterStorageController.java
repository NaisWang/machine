package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.MachineTraceServiceImpl;
import com.example.server.service.impl.MarketReturnEnterStorageServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import springfox.documentation.schema.AlternateTypeRule;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
@RestController
@RequestMapping("/market/return/enter-storage")
public class MarketReturnEnterStorageController {

	@Autowired
	private MarketReturnEnterStorageServiceImpl marketReturnEnterStorageService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@ApiModelProperty("获取所有销退入库单据")
	@GetMapping("/")
	public RespBean getMarketReturnEnterStorageReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																										 @RequestParam(defaultValue = "10") Integer size,
																										 MarketReturnEnterStorage marketReturnEnterStorage) {
		RespPageBean respPageBean = marketReturnEnterStorageService.getMarketReturnEnterStorageReceipt(currentPage, size, marketReturnEnterStorage);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiOperation("创建销退入库单")
	@PutMapping("/")
	@Transactional
	public RespBean createMarketReturnEnterStorageReceipt(@RequestBody MarketReturnEnterStorage marketReturnEnterStorage, Authentication authentication) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {

			marketReturnEnterStorage.setCreateTime(now);
			marketReturnEnterStorage.setIsRelease(0);
			marketReturnEnterStorage.setOperateEmpId(empId);
			if (marketReturnEnterStorageService.save(marketReturnEnterStorage)) {
				logService.save(new Log(empId, "添加销退入库单", "", LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			logService.save(new Log(empId, "添加销退入库单", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "添加销退入库单", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改销退入库单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody MarketReturnEnterStorage receipt, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		if (!empId.equals(receipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该销退入库单已添加");
		}

		MarketReturnEnterStorage afterMarketReturnEnterStorage = new MarketReturnEnterStorage();
		afterMarketReturnEnterStorage.setComment(receipt.getComment());

		if (marketReturnEnterStorageService.update(afterMarketReturnEnterStorage, new UpdateWrapper<MarketReturnEnterStorage>().eq("id", receipt.getId()))) {
			logService.save(new Log(empId, "修改销退入库单", "", LocalDateTime.now(), 0));
			return RespBean.success("更新成功");
		}
		logService.save(new Log(empId, "修改销退入库单", "", LocalDateTime.now(), 1));
		return RespBean.error("更新失败");
	}

	@ApiOperation("往销退入库单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		MarketReturnEnterStorage marketReturnEnterStorage = marketReturnEnterStorageService.getById(receiptId);
		try {
			if (!empId.equals(marketReturnEnterStorage.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnEnterStorage.getIsRelease() == 1) {
				return RespBean.error("该销退入库单已经发布了");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));
			List<MachineTrace> machineTraces = new ArrayList<>();
			for (Machine machine : machines) {
				//状态判断
				if (machine.getStatusId() != 14) {
					return RespBean.error("数据有误");
				}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(28);
				machine.setMarketReturnEnterStorageReceiptId(receiptId);
				machine.setOperateEmpId(empId);
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId()));
			}

			if (machineService.updateBatchById(machines)) {
				if (machineTraceService.saveBatch(machineTraces)) {
					logService.save(new Log(empId, "往销退入库单中添加机器", "", LocalDateTime.now(), 0));
					return RespBean.success("添加成功");
				}
			}
			logService.save(new Log(empId, "往销退入库单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "往销退入库单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除销退入库单中的机器")
	@DeleteMapping("/deleteMachine/")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(id);

		try {
			MarketReturnEnterStorage marketReturnEnterStorage = marketReturnEnterStorageService.getById(machine.getMarketReturnEnterStorageReceiptId());
			if (!empId.equals(marketReturnEnterStorage.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnEnterStorage.getIsRelease() == 1) {
				return RespBean.error("销退入库单已经提交");
			}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setMarketReturnEnterStorageReceiptId(0);

			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineTraceService.save(new MachineTrace(machine.getNumber(), machine.getStatusId(), -1, now, empId, machine.getComment(), machine.getStorageLocationId()))) {
					logService.save(new Log(empId, "删除销退入库单中机器", "", LocalDateTime.now(), 0));
					return RespBean.success("删除成功");
				}
			}
			logService.save(new Log(empId, "删除销退入库单中机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除销退入库单中机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("发布销退入库单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseMarketReturnEnterStorageReceipt(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			MarketReturnEnterStorage marketReturnEnterStorage = marketReturnEnterStorageService.getById(receiptId);
			if (!empId.equals(marketReturnEnterStorage.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnEnterStorage.getIsRelease() == 1) {
				return RespBean.error("该销退入库单已经发布了");
			}

			List<Machine> machines = machineService.list(new UpdateWrapper<Machine>().eq("market_return_enter_storage_receipt_id", receiptId));
			if (machines.size() == 0) {
				return RespBean.error("该销退入库单为空，不能发布");
			}

			if (marketReturnEnterStorageService.update(new MarketReturnEnterStorage(), new UpdateWrapper<MarketReturnEnterStorage>().eq("id", receiptId).set("is_release", 1).set("release_time", now))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("market_return_enter_storage_receipt_id", receiptId).set("status_id", 29).set("storage_location_id", marketReturnEnterStorage.getStorageLocationId()))) {
					List<MachineTrace> machineTraces = new ArrayList<>();
					for (Machine machine : machines) {
						MachineTrace machineTrace = new MachineTrace(machine.getNumber(), 29, receiptId, now, empId, machine.getComment(), marketReturnEnterStorage.getStorageLocationId());
						machineTrace.setStorageLocationId(marketReturnEnterStorage.getStorageLocationId());
						machineTraces.add(machineTrace);
					}
					if (machineTraceService.saveBatch(machineTraces)) {
						logService.save(new Log(empId, "发布销退入库单", "销退入库单号为：" + receiptId, now, 0));
						return RespBean.success("发布成功");
					}
				}
			}
			logService.save(new Log(empId, "发布销退入库单", "销退入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "发布销退入库单", "销退入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("删除销退入库单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		MarketReturnEnterStorage marketReturnEnterStorage = marketReturnEnterStorageService.getById(receiptId);
		try {
			if (!empId.equals(marketReturnEnterStorage.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			//if (marketReturnEnterStorage.getIsRelease() == 1) {
			//		return RespBean.error("该销退入库单已经提交了, 无法再删除");
			//	}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("market_return_enter_storage_receipt_id", receiptId));
			List<MachineTrace> machineTraces = new ArrayList<>();

			for (Machine machine : machines) {
				machine.setMarketReturnEnterStorageReceiptId(0);
				machine.setStatusId(machine.getPreviousStatusId());
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId()));
			}
			if (marketReturnEnterStorageService.removeById(receiptId)) {
				if (machines.size() == 0 || machineService.updateBatchById(machines)) {
					if (machineTraceService.saveBatch(machineTraces)) {
						logService.save(new Log(empId, "删除销退入库单", "销退入库单号为：" + receiptId, now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除销退入库单", "销退入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除销退入库单", "销退入库单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		}
	}
}
