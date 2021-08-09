package com.example.server.controller;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.MachineTraceServiceImpl;
import com.example.server.service.impl.MarketOrderReceiptServiceImpl;
import com.example.server.utils.JudgeCompleteDeliverIntention;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
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
 * @since 2021-07-11
 */
@RestController
@RequestMapping("/machine/market/order")
public class MarketOrderReceiptController {

	@Autowired
	private MarketOrderReceiptServiceImpl marketOrderReceiptService;
	@Autowired
	private MachineServiceImpl machineService;
	@Autowired
	private LogServiceImpl logService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@ApiOperation("获取所有销售订单")
	@GetMapping("/")
	public RespBean getMarketOrderReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																				@RequestParam(defaultValue = "10") Integer size,
																				MarketOrderReceipt marketOrderReceipt, Integer scope, Authentication authentication) {
		//Integer empId = ((Employee) authentication.getPrincipal()).getId();
		//marketOrderReceipt.setOperateEmpId(empId);
		//if (scope == 1) {
		//	if (((Employee) authentication.getPrincipal()).getAuthorities().contains("ROLE_marketPlus")) {
		//		marketOrderReceipt.setOperateEmpId(null);
		//	}
		//}
		//System.out.println(((Employee) authentication.getPrincipal()).getAuthorities());
		RespPageBean respPageBean = marketOrderReceiptService.getMarketOrderReceipt(currentPage, size, marketOrderReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

	//@ApiModelProperty("创建销售订单")
	//@PutMapping("/")
	//@Transactional
	//public RespBean createMarketOrderReceipt(@RequestBody Map<String, String> marketOrderReceiptMachine, Authentication authentication) throws Exception {
	//	ObjectMapper objectMapper = new ObjectMapper();

	//	MarketOrderReceipt marketOrderReceipt = objectMapper.readValue(marketOrderReceiptMachine.get("marketOrderReceipt"), MarketOrderReceipt.class);
	//	List<Machine> machines = objectMapper.readValue(marketOrderReceiptMachine.get("machine"), new TypeReference<List<Machine>>() {
	//	});

	//	System.out.println(marketOrderReceipt);
	//	System.out.println(machines);

	//	LocalDate nowDate = LocalDate.now();
	//	marketOrderReceipt.setMarketOrderDate(nowDate);

	//	Integer empId = ((Employee) authentication.getPrincipal()).getId();

	//	if (marketOrderReceiptService.save(marketOrderReceipt)) {
	//		Integer marketOrderReceiptId = marketOrderReceipt.getMarketOrder();
	//		for (Machine machine : machines) {
	//			machine.setMarketOrderId(marketOrderReceiptId);
	//			machine.setStatusId(6);
	//			machine.setOperateEmpId(empId);
	//		}
	//		if (machineService.updateBatchById(machines)) {
	//			logService.save(new Log(empId, "创建销售订单", marketOrderReceiptMachine.toString(), LocalDateTime.now(), 0));
	//			return RespBean.success("添加成功");
	//		}
	//	}
	//	logService.save(new Log(empId, "创建销售订单", marketOrderReceiptMachine.toString(), LocalDateTime.now(), -1));
	//	throw new RuntimeException("添加失败");
	//}

	@ApiOperation("创建销售订单")
	@PutMapping("/")
	@Transactional
	public RespBean createMarketOrderReceipt(@RequestBody MarketOrderReceipt marketOrderReceipt, Authentication authentication) throws Exception {
		try {
			LocalDateTime now = LocalDateTime.now();
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			marketOrderReceipt.setCreateTime(now);
			marketOrderReceipt.setIsRelease(0);
			marketOrderReceipt.setOperateEmpId(empId);
			if (marketOrderReceiptService.save(marketOrderReceipt)) {
				logService.save(new Log(empId, "添加销售订单货单", "", LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("修改销售订单信息")
	@PutMapping("/modifyReceipt")
	public RespBean modifyReceipt(@RequestBody MarketOrderReceipt receipt, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();

		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该销售单已提交");
		}
		if (!empId.equals(receipt.getOperateEmpId())) {
			return RespBean.error("你没有权限操作该单据");
		}

		MarketOrderReceipt afterMarketOrderReceipt = new MarketOrderReceipt();
		afterMarketOrderReceipt.setComment(receipt.getComment());

		if (marketOrderReceiptService.update(afterMarketOrderReceipt, new UpdateWrapper<MarketOrderReceipt>().eq("market_order", receipt.getMarketOrder()))) {
			logService.save(new Log(empId, "修改销售订单货单", "", LocalDateTime.now(), 0));
			return RespBean.success("更新成功");
		}
		logService.save(new Log(empId, "修改销售订单货单", "", LocalDateTime.now(), 1));
		return RespBean.error("更新失败");
	}

	@ApiOperation("往销售订单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		MarketOrderReceipt marketOrderReceipt = marketOrderReceiptService.getById(receiptId);
		try {

			if (!empId.equals(marketOrderReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (marketOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售单已经发布了");
			}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));
			List<MachineTrace> machineTraces = new ArrayList<>();

			for (Machine machine : machines) {
				if (machine.getStatusId() != 26) {
					return RespBean.error("该机器的状态不是已上架、待出库状态！");
				}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(25);
				machine.setMarketOrderId(receiptId);
				machine.setOperateEmpId(empId);
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
			}

			if (machineService.updateBatchById(machines)) {
				if (machineTraceService.saveBatch(machineTraces)) {
					logService.save(new Log(empId, "往销售订单货单中添加机器", "", LocalDateTime.now(), 0));
					return RespBean.success("添加成功");
				}
			}
			logService.save(new Log(empId, "往销售订单货单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "往销售订单货单中添加机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("添加失败");
		}
	}

	@ApiOperation("删除销售订单中的机器")
	@DeleteMapping("/deleteMachine")
	@Transactional
	public RespBean deleteMachine(Integer id, Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		Machine machine = machineService.getById(id);
		MarketOrderReceipt marketOrderReceipt = marketOrderReceiptService.getById(machine.getMarketOrderId());
		try {

			if (!empId.equals(marketOrderReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}
			if (marketOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售单已经发布了");
			}
			machine.setStatusId(machine.getPreviousStatusId());
			machine.setMarketOrderId(0);

			if (machineService.update(machine, new UpdateWrapper<Machine>().eq("id", machine.getId()))) {
				if (machineTraceService.save(new MachineTrace(machine.getNumber(), machine.getStatusId(), -1, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()))) {
					logService.save(new Log(empId, "删除销售订单货单中机器", "", LocalDateTime.now(), 0));
					return RespBean.success("删除成功");
				}
			}
			logService.save(new Log(empId, "删除销售订单货单中机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除销售订单货单中机器", "", LocalDateTime.now(), 1));
			throw new RuntimeException("删除失败");
		}
	}

	@ApiOperation("发布销售单")
	@GetMapping("/release")
	@Transactional
	public RespBean releaseMarketOrderReceipt(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		LocalDateTime now = LocalDateTime.now();
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		try {

			MarketOrderReceipt marketOrderReceipt = marketOrderReceiptService.getById(receiptId);
			if (!empId.equals(marketOrderReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (marketOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售单已经发布了");
			}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("market_order_id", receiptId));
			if (machines.size() == 0) {
				return RespBean.error("该销售单为空，不能发布");
			}

			if (marketOrderReceiptService.update(new MarketOrderReceipt(), new UpdateWrapper<MarketOrderReceipt>().eq("market_order", receiptId).set("is_release", 1).set("release_time", now))) {
				if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("market_order_id", receiptId).set("status_id", 13).set("storage_location_id", null))) {
					List<MachineTrace> machineTraces = new ArrayList<>();
					for (Machine machine : machines) {
						machineTraces.add(new MachineTrace(machine.getNumber(), 13, receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
					}
					if (machineTraceService.saveBatch(machineTraces)) {
						if (JudgeCompleteDeliverIntention.judgeIsComplete(machines, 9)) {
							logService.save(new Log(empId, "发布销售单", "销售单号为：" + receiptId, now, 0));
							return RespBean.success("发布成功");
						}
					}
				}
			}
			logService.save(new Log(empId, "发布销售单", "销售单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "发布销售单", "销售单号为：" + receiptId, now, 1));
			throw new RuntimeException("发布失败");
		}
	}

	@ApiOperation("删除采购退货单")
	@DeleteMapping("/")
	@Transactional
	public RespBean deleteReceipt(Integer receiptId, Authentication authentication) {
		if (receiptId == 0) {
			return RespBean.error("数据错误");
		}
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		LocalDateTime now = LocalDateTime.now();
		MarketOrderReceipt marketOrderReceipt = marketOrderReceiptService.getById(receiptId);
		try {

			if (!empId.equals(marketOrderReceipt.getOperateEmpId())) {
				return RespBean.error("你没有权限操作该单据");
			}

			if (marketOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售货单已经提交了, 无法再删除");
			}
			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().eq("market_order_id", receiptId));
			List<MachineTrace> machineTraces = new ArrayList<>();
			for (Machine machine : machines) {
				machine.setMarketOrderId(0);
				machine.setStatusId(machine.getPreviousStatusId());
				machineTraces.add(new MachineTrace(machine.getNumber(), machine.getStatusId(), receiptId, now, empId, machine.getComment(), machine.getStorageLocationId(), machine.getIsUpShelf()));
			}
			if (marketOrderReceiptService.removeById(receiptId)) {
				if (machines.size() == 0 || machineService.updateBatchById(machines)) {
					if (machineTraceService.saveBatch(machineTraces)) {
						logService.save(new Log(empId, "删除销售单", "销售单号为：" + receiptId, now, 0));
						return RespBean.success("删除成功");
					}
				}
			}
			logService.save(new Log(empId, "删除销售单", "销售单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		} catch (Exception e) {
			e.printStackTrace();
			logService.save(new Log(empId, "删除销售单", "销售单号为：" + receiptId, now, 1));
			throw new RuntimeException("删除失败");
		}
	}
}

