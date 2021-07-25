package com.example.server.controller;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.MarketOrderReceiptServiceImpl;
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

	@ApiOperation("获取所有销售订单")
	@GetMapping("/")
	public RespBean getMarketOrderReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																				@RequestParam(defaultValue = "10") Integer size,
																				MarketOrderReceipt marketOrderReceipt) {
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
			LocalDate now = LocalDate.now();
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			marketOrderReceipt.setMarketOrderDate(now);
			marketOrderReceipt.setIsRelease(0);
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
	public RespBean modifyReceipt(@RequestBody MarketOrderReceipt receipt) {
		if (receipt.getIsRelease() == 1) {
			return RespBean.error("该销售单已提交");
		}

		MarketOrderReceipt afterMarketOrderReceipt = new MarketOrderReceipt();
		afterMarketOrderReceipt.setComment(receipt.getComment());

		if (marketOrderReceiptService.update(afterMarketOrderReceipt, new UpdateWrapper<MarketOrderReceipt>().eq("market_order", receipt.getMarketOrder()))) {
			return RespBean.success("更新成功");
		}
		return RespBean.error("更新失败");
	}

	@ApiOperation("往销售订单中添加机器")
	@PutMapping("/addMachine")
	@Transactional
	public RespBean addMachine(@RequestBody Integer[] ids, Integer receiptId, Authentication authentication) {
		try {
			Integer empId = ((Employee) authentication.getPrincipal()).getId();
			MarketOrderReceipt marketOrderReceipt = marketOrderReceiptService.getById(receiptId);
			if (marketOrderReceipt.getIsRelease() == 1) {
				return RespBean.error("该销售单已经发布了");
			}

			List<Machine> machines = machineService.list(new QueryWrapper<Machine>().in("id", ids));

			for (Machine machine : machines) {
				//if (machine.getStatusId() != 26) {
				//		return RespBean.error("该机器的状态不是已上架、待出库状态！");
				//	}
				machine.setPreviousStatusId(machine.getStatusId());
				machine.setStatusId(25);
				machine.setMarketOrderId(receiptId);
				machine.setOperateEmpId(empId);
			}

			if (machineService.updateBatchById(machines)) {
				return RespBean.success("添加成功");
			}
			throw new RuntimeException("添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加失败");
		}
	}

}

