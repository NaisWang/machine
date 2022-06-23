package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.MarketOrderReceiptToMachine;
import com.example.server.service.impl.MarketOrderReceiptToMachineServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
@RestController
@RequestMapping("/market-order-receipt-to-machine")
public class MarketOrderReceiptToMachineController {

	@Autowired
	private MarketOrderReceiptToMachineServiceImpl marketOrderReceiptToMachineService;

	@ApiOperation("获取销售订单中的机器")
	@GetMapping("/")
	public RespBean getMarketOrderReceiptToMachine(@RequestParam(defaultValue = "1") Integer currentPage,
																								 @RequestParam(defaultValue = "10") Integer size,
																								 Integer receiptId) {
		RespPageBean respPageBean = marketOrderReceiptToMachineService.getMarketOrderReceiptToMachine(currentPage, size, receiptId);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiOperation("修改销售订单中的机器")
	@PutMapping("/")
	public RespBean modifyMarketOrderReceiptToMachine(MarketOrderReceiptToMachine marketOrderReceiptToMachine) {
		if (marketOrderReceiptToMachineService.update(marketOrderReceiptToMachine, new UpdateWrapper<MarketOrderReceiptToMachine>().eq("receipt_id", marketOrderReceiptToMachine.getReceiptId()).eq("machine_id", marketOrderReceiptToMachine.getMachineId()))) {
			return RespBean.success("修改成功");
		}
		return RespBean.error("修改成功");
	}

}
