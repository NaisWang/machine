package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-12
 */
@RestController
@RequestMapping("/machine/market/return")
public class MarketReturnReceiptController {

	@Autowired
	private MarketReturnReceiptServiceImpl marketReturnReceiptService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;


	@ApiOperation("获取所有销售退货订单")
	@GetMapping("/")
	public RespBean getMarketReturnReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																				 @RequestParam(defaultValue = "10") Integer size,
																				 MarketReturnReceipt marketReturnReceipt) {
		RespPageBean respPageBean = marketReturnReceiptService.getMarketReturnReceipt(currentPage, size, marketReturnReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

	//@ApiModelProperty("创建销售退货订单")
	//@PutMapping("/")
	//@Transactional
	//public RespBean createMarketReturnReceipt(@RequestBody Map<String, String> marketReturnReceiptMachine, Authentication authentication) throws Exception {
	//	ObjectMapper objectMapper = new ObjectMapper();

	//	MarketReturnReceipt marketReturnReceipt = objectMapper.readValue(marketReturnReceiptMachine.get("marketReturnReceipt"), MarketReturnReceipt.class);
	//	List<Machine> machines = objectMapper.readValue(marketReturnReceiptMachine.get("machine"), new TypeReference<List<Machine>>() {
	//	});

	//	System.out.println(marketReturnReceipt);
	//	System.out.println(machines);

	//	LocalDate nowDate = LocalDate.now();
	//	marketReturnReceipt.setMarketReturnDate(nowDate);

	//	Integer empId = ((Employee) authentication.getPrincipal()).getId();

	//	if (marketReturnReceiptService.save(marketReturnReceipt)) {
	//		Integer id = marketReturnReceipt.getMarketReturnOrder();
	//		for (Machine machine : machines) {
	//			machine.setMarketReturnReceiptId(id);
	//			machine.setOperateEmpId(empId);
	//			machine.setStatusId(9);
	//		}
	//		if (machineService.updateBatchById(machines)) {
	//			logService.save(new Log(empId, "创建销售退货订单", marketReturnReceipt.toString(), LocalDateTime.now(), 0));
	//			return RespBean.success("添加成功");
	//		}
	//	}
	//	logService.save(new Log(empId, "创建销售退货订单", marketReturnReceipt.toString(), LocalDateTime.now(), 0));
	//	throw new RuntimeException("添加失败");
	//}

	@ApiOperation("创建销售订单")
	@PutMapping("/")
	@Transactional
	public RespBean createMarketReturnReceipt(@RequestBody MarketReturnReceipt receipt, Authentication authentication) throws Exception {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {
			LocalDateTime now = LocalDateTime.now();
			receipt.setCreateTime(now);
			receipt.setOperateEmpId(empId);
			receipt.setIsRelease(0);
			if (marketReturnReceiptService.save(receipt)) {
				logService.save(new Log(empId, "添加销售退货货单", "", LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			logService.save(new Log(empId, "添加销售退货货单", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "添加销售退货货单", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改销售订单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody MarketReturnReceipt receipt, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		if (!empId.equals(receipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该销售单已提交");
		}

		MarketReturnReceipt afterMarketReturnReceipt = new MarketReturnReceipt();

		afterMarketReturnReceipt.setComment(receipt.getComment());

		if (marketReturnReceiptService.update(afterMarketReturnReceipt, new UpdateWrapper<MarketReturnReceipt>().eq("market_return_order", receipt.getMarketReturnOrder()))) {
			logService.save(new Log(empId, "修改销售退货货单信息", "", LocalDateTime.now(), 0));
			return RespBean.success("更新成功");
		}
		logService.save(new Log(empId, "修改销售退货货单信息", "", LocalDateTime.now(), 1));
		return RespBean.error("更新失败");
	}

	@ApiOperation("往销售退货订单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		MarketReturnReceipt marketReturnReceipt = marketReturnReceiptService.getById(receiptId);
		try {
			if (!empId.equals(marketReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售退货单已经发布了");
			}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));
			List<MachineTrace> machineTraceList = new ArrayList<>();

			for (Machine machine : machines) {
				if (machine.getStatusId() != 13) {
					return RespBean.error("该机器的状态不是已出库状态！");
				}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(27);
				machine.setMarketReturnReceiptId(receiptId);
				machine.setOperateEmpId(empId);
				machineTraceList.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
			}

			if (machineService.updateBatchById(machines)) {
				if (machineTraceService.saveBatch(machineTraceList)) {
					logService.save(new Log(empId, "往销售退货单中添加机器", "", LocalDateTime.now(), 0));
					return RespBean.success("添加成功");
				}
			}
			logService.save(new Log(empId, "往销售退货单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "往销售退货单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除销售退货订单中的机器")
	@DeleteMapping("/deleteMachine")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(id);
		MarketReturnReceipt marketReturnReceipt = marketReturnReceiptService.getById(machine.getMarketOrderId());
		try {
			if (!empId.equals(marketReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("销售退货单已经提交");
			}

			machine.setStatusId(machine.getPreviousStatusId());
			machine.setMarketReturnReceiptId(0);

			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineTraceService.save(new MachineTrace(machine.getNumber(), machine.getStatusId(), -1, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
					logService.save(new Log(empId, "删除销售退货单中添加机器", "", LocalDateTime.now(), 0));
					return RespBean.success("删除成功");
				}
			}
			logService.save(new Log(empId, "删除销售退货单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除销售退货单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("发布销售退货单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseMarketReturnReceipt(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		MarketReturnReceipt marketReturnReceipt = marketReturnReceiptService.getById(receiptId);
		try {
			if (!empId.equals(marketReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售退货单已经发布了");
			}

			List<Machine> machines = machineService.list(new UpdateWrapper<Machine>().eq("market_return_receipt_id", receiptId));
			if (machines.size() == 0) {
				return RespBean.error("该销售退货单为空，不能发布");
			}

			if (marketReturnReceiptService.update(new MarketReturnReceipt(), new UpdateWrapper<MarketReturnReceipt>().eq("market_return_order", receiptId).set("is_release", 1).set("release_time", now))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("market_return_receipt_id", receiptId).set("status_id", 14))) {
					List<MachineTrace> machineTraces = new ArrayList<>();
					for (Machine machine : machines) {
						machineTraces.add(new MachineTrace(machine.getNumber(), 14, receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
					}
					if (machineTraceService.saveBatch(machineTraces)) {
						logService.save(new Log(empId, "发布销售退货单", "销售退货单号为：" + receiptId, now, 0));
						return RespBean.success("发布成功");
					}
				}
			}
			logService.save(new Log(empId, "发布销售退货单", "销售退货单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "发布销售退货单", "销售退货单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("删除销售退货单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		MarketReturnReceipt marketReturnReceipt = marketReturnReceiptService.getById(receiptId);
		try {
			if (!empId.equals(marketReturnReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketReturnReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售退货单已经提交了, 无法再删除");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("market_return_receipt_id", receiptId));
			List<MachineTrace> machineTraces = new ArrayList<>();

			for (Machine machine : machines) {
				machine.setStatusId(machine.getPreviousStatusId());
				machine.setMarketReturnReceiptId(0);
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
			}
			if (marketReturnReceiptService.removeById(receiptId)) {
				if (machines.size() == 0 || machineService.updateBatchById(machines)) {
					if (machineTraceService.saveBatch(machineTraces)) {
						logService.save(new Log(empId, "删除销售退货单", "销售退货单号为：" + receiptId, now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除销售退货单", "销售退货单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除销售退货单", "销售退货单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		}
	}
}
