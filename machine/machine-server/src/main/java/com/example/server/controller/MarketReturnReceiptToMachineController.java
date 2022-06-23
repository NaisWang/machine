package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.MarketReturnReceiptToMachine;
import com.example.server.service.impl.MarketReturnReceiptToMachineServiceImpl;
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
@RequestMapping("/market-return-receipt-to-machine")
public class MarketReturnReceiptToMachineController {

	@Autowired
	private MarketReturnReceiptToMachineServiceImpl marketReturnReceiptToMachineService;

	@ApiOperation("获取销售退货订单中的机器")
	@GetMapping("/")
	public RespBean getMarketReturnReceiptToMachine(@RequestParam(defaultValue = "1") Integer currentPage,
																									@RequestParam(defaultValue = "10") Integer size,
																									Integer receiptId) {
		RespPageBean respPageBean = marketReturnReceiptToMachineService.getMarketReturnReceiptToMachine(currentPage, size, receiptId);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiOperation("修改销售退货订单中的机器")
	@PutMapping("/")
	public RespBean modifyMarketReturnReceiptToMachine(MarketReturnReceiptToMachine marketReturnReceiptToMachine) {
		if (marketReturnReceiptToMachineService.update(marketReturnReceiptToMachine, new UpdateWrapper<MarketReturnReceiptToMachine>().eq("receipt_id", marketReturnReceiptToMachine.getReceiptId()).eq("machine_id", marketReturnReceiptToMachine.getMachineId()))) {
			return RespBean.success("修改成功");
		}
		return RespBean.error("修改成功");
	}
}
