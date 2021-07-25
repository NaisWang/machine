package com.example.server.controller;


import com.example.server.pojo.*;
import com.example.server.service.impl.LogServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.MarketOrderReceiptServiceImpl;
import com.example.server.service.impl.MarketReturnReceiptServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
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

	@ApiOperation("获取所有销售退货订单")
	@GetMapping("/")
	public RespBean getMarketReturnReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																				 @RequestParam(defaultValue = "10") Integer size,
																				 MarketReturnReceipt marketReturnReceipt) {
		RespPageBean respPageBean = marketReturnReceiptService.getMarketReturnReceipt(currentPage, size, marketReturnReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

	@ApiModelProperty("创建销售退货订单")
	@PutMapping("/")
	@Transactional
	public RespBean createMarketReturnReceipt(@RequestBody Map<String, String> marketReturnReceiptMachine, Authentication authentication) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		MarketReturnReceipt marketReturnReceipt = objectMapper.readValue(marketReturnReceiptMachine.get("marketReturnReceipt"), MarketReturnReceipt.class);
		List<Machine> machines = objectMapper.readValue(marketReturnReceiptMachine.get("machine"), new TypeReference<List<Machine>>() {
		});

		System.out.println(marketReturnReceipt);
		System.out.println(machines);

		LocalDate nowDate = LocalDate.now();
		marketReturnReceipt.setMarketReturnDate(nowDate);

		Integer empId = ((Employee) authentication.getPrincipal()).getId();

		if (marketReturnReceiptService.save(marketReturnReceipt)) {
			Integer id = marketReturnReceipt.getMarketReturnOrder();
			for (Machine machine : machines) {
				machine.setMarketReturnReceiptId(id);
				machine.setOperateEmpId(empId);
				machine.setStatusId(9);
			}
			if (machineService.updateBatchById(machines)) {
				logService.save(new Log(empId, "创建销售退货订单", marketReturnReceipt.toString(), LocalDateTime.now(), 0));
				return RespBean.success("添加成功");
			}
		}
		logService.save(new Log(empId, "创建销售退货订单", marketReturnReceipt.toString(), LocalDateTime.now(), 0));
		throw new RuntimeException("添加失败");
	}
}
