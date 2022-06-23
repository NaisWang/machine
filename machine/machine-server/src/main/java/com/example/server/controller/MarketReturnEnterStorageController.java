package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.JudgeCompleteDeliverIntention;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Autowired
	private DeliverReceiptServiceImpl deliverReceiptService;
	@Autowired
	private DeliverMachineServiceImpl deliverMachineService;
	@Autowired
	private MarketReturnEnterStorageToMachineServiceImpl marketReturnEnterStorageToMachineService;

	@ApiModelProperty("获取所有销退入库单据")
	@GetMapping("/")
	public RespBean getMarketReturnEnterStorageReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																										 @RequestParam(defaultValue = "10") Integer size,
																										 MarketReturnEnterStorage marketReturnEnterStorage) {
		RespPageBean respPageBean = marketReturnEnterStorageService.getMarketReturnEnterStorageReceipt(currentPage, size, marketReturnEnterStorage);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiModelProperty("获取所有销退入库单据中的机器")
	@GetMapping("/machines")
	public RespBean getMarketReturnEnterStorageReceiptToMachines(@RequestParam(defaultValue = "1") Integer currentPage,
																															 @RequestParam(defaultValue = "10") Integer size,
																															 Integer receiptId) {
		RespPageBean respPageBean = marketReturnEnterStorageToMachineService.getMarketReturnEnterStorageReceipt(currentPage, size, receiptId);
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
			List<MarketReturnEnterStorageToMachine> marketReturnEnterStorageToMachines = new ArrayList<>();

			for (Machine machine : machines) {
				//状态判断
				if (machine.getStatusId() != 14) {
					return RespBean.error("数据有误");
				}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(28);
				machine.setMarketReturnEnterStorageReceiptId(receiptId);
				machine.setOperateEmpId(empId);
				machineTraces.add(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));

				marketReturnEnterStorageToMachines.add(new MarketReturnEnterStorageToMachine(null, receiptId, machine.getNumber(), machine.getSku(), machine.getId()));

			}

			if (machineService.updateBatchById(machines)) {
				if (machineTraceService.saveBatch(machineTraces)) {
					if (marketReturnEnterStorageToMachineService.saveBatch(marketReturnEnterStorageToMachines)) {
						logService.save(new Log(empId, "往销退入库单中添加机器", "", LocalDateTime.now(), 0));
						return RespBean.success("添加成功");
					}
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
	public RespBean deleteMachine(Integer id, Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(id);

		try {
			MarketReturnEnterStorage marketReturnEnterStorage = marketReturnEnterStorageService.getById(receiptId);
			if (!empId.equals(marketReturnEnterStorage.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnEnterStorage.getIsRelease() == 1) {
				return RespBean.error("销退入库单已经提交");
			}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setMarketReturnEnterStorageReceiptId(0);

			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineTraceService.save(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), -1, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
					if (marketReturnEnterStorageToMachineService.remove(new QueryWrapper<MarketReturnEnterStorageToMachine>().eq("receipt_id", receiptId).eq("machine_id", id))) {
						logService.save(new Log(empId, "删除销退入库单中机器", "", LocalDateTime.now(), 0));
						return RespBean.success("删除成功");
					}
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
						MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), 29, receiptId, now, empId, machine.getComment(), marketReturnEnterStorage.getStorageLocationId(), machine.getIsUpShelf());
						machineTrace.setStorageLocationId(marketReturnEnterStorage.getStorageLocationId());
						machineTraces.add(machineTrace);
					}
					if (machineTraceService.saveBatch(machineTraces)) {
						if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 8)) {
							logService.save(new Log(empId, "发布销退入库单", "销退入库单号为：" + receiptId, now, 0));
							return RespBean.success("发布成功");
						}
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
				machineTraces.add(new MachineTrace(machine.getId(), machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
			}
			if (marketReturnEnterStorageService.removeById(receiptId)) {

				if (marketReturnEnterStorageToMachineService.remove(new QueryWrapper<MarketReturnEnterStorageToMachine>().eq("receipt_id", receiptId))) {
					if (machines.size() == 0 || machineService.updateBatchById(machines)) {
						if (machineTraceService.saveBatch(machineTraces)) {
							logService.save(new Log(empId, "删除销退入库单", "销退入库单号为：" + receiptId, now, 0));
							return RespBean.success("删除成功");
						}
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

	@ApiOperation("创建、添加、发布入库单")
	@PutMapping("/create-add-release")
	@Transactional
	public RespBean createAddRelease(@RequestBody String[] numbers, Integer marketReturnEnterStorageReceiptId, Integer storageLocationId, Integer deliverReceiptId, Authentication authentication) {
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();

		if (storageLocationId == null) {
			return RespBean.error("没有选择库位");
		}


		try {
			//如果没有入库单，则创建并发布入库单
			if (marketReturnEnterStorageReceiptId == null) {
				MarketReturnEnterStorage marketReturnEnterStorageReceipt = new MarketReturnEnterStorage();
				marketReturnEnterStorageReceipt.setCreateTime(now);
				marketReturnEnterStorageReceipt.setReleaseTime(now);
				marketReturnEnterStorageReceipt.setOperateEmpId(empId);
				marketReturnEnterStorageReceipt.setIsRelease(1);
				marketReturnEnterStorageReceipt.setStorageLocationId(storageLocationId);
				if (marketReturnEnterStorageService.save(marketReturnEnterStorageReceipt)) {
					marketReturnEnterStorageReceiptId = marketReturnEnterStorageReceipt.getId();
				} else {
					throw new RuntimeException("运行错误");
				}
			}

			DeliverReceipt deliverReceipt = deliverReceiptService.getById(deliverReceiptId);
			deliverReceipt.setStorageLocationId(storageLocationId);
			deliverReceipt.setEnterStorageReceiptId(marketReturnEnterStorageReceiptId);
			if (!deliverReceiptService.update(deliverReceipt, new UpdateWrapper<DeliverReceipt>().eq("deliver_receipt_id", deliverReceiptId))) {
				throw new RuntimeException("运行错误");
			}

			//接收并处理机器
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("number", numbers));
			List<MachineTrace> machineTraces = new ArrayList<>();
			List<MarketReturnEnterStorageToMachine> marketReturnEnterStorageToMachines = new ArrayList<>();

			QueryWrapper<DeliverMachine> deliverMachineQueryWrapper = new QueryWrapper<>();
			for (Machine machine : machines) {

				if (!machine.getDeliverReceiptId().equals(deliverReceiptId)) {
					throw new RuntimeException("运行错误");
				}

				machine.setMarketReturnEnterStorageReceiptId(marketReturnEnterStorageReceiptId);
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(29);
				machine.setDeliverReceiptId(0);
				machine.setNeedCompleteDeliverReceiptId(0);
				machine.setOperateEmpId(empId);

				marketReturnEnterStorageToMachines.add(new MarketReturnEnterStorageToMachine(null, marketReturnEnterStorageReceiptId, machine.getNumber(), machine.getSku(), machine.getId()));

				Map<String, Integer> queryMap = new HashMap<>();
				queryMap.put("machine_id", machine.getId());
				queryMap.put("deliver_receipt_id", deliverReceiptId);
				deliverMachineQueryWrapper = deliverMachineQueryWrapper.or().allEq(queryMap);

				MachineTrace machineTrace = new MachineTrace(machine.getId(), machine.getNumber(), 29, marketReturnEnterStorageReceiptId, now, empId, machine.getComment(), storageLocationId, machine.getIsUpShelf());
				machineTraces.add(machineTrace);

			}

			List<DeliverMachine> deliverMachines = deliverMachineService.list(deliverMachineQueryWrapper);
			for (DeliverMachine deliverMachine : deliverMachines) {
				deliverMachine.setReceiveEmpId(empId);
				deliverMachine.setStatus(2);
				deliverMachine.setIsComplete(1);
				deliverMachine.setReceiveDate(now);
			}

			if (machineService.updateBatchById(machines)) {
				if (deliverMachineService.updateBatchById(deliverMachines)) {
					if (marketReturnEnterStorageToMachineService.saveBatch(marketReturnEnterStorageToMachines)) {
						if (machineTraceService.saveBatch(machineTraces)) {
							return RespBean.success("操作成功", marketReturnEnterStorageReceiptId);
						}
					}
				}
			}
			throw new RuntimeException("操作失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("操作失败");
		}
	}
}
