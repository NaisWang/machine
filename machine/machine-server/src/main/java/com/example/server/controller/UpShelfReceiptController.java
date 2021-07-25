package com.example.server.controller;


import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.PurchaseOrderReceipt;
import com.example.server.pojo.UpShelfReceipt;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.UpShelfReceiptServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-17
 */
@RestController
@RequestMapping("/machine/shelf/up")
public class UpShelfReceiptController {

	@Autowired
	private UpShelfReceiptServiceImpl upShelfReceiptService;

	@ApiModelProperty("获取上架单据")
	@GetMapping("/")
	public RespBean getUpShelfReceipt(@RequestParam(defaultValue = "1") Integer currentPage,
																		@RequestParam(defaultValue = "10") Integer size,
																		LocalDate[] localDateScope,
																		UpShelfReceipt upShelfReceipt) {
		RespPageBean respPageBean = upShelfReceiptService.getUpShelfReceipt(currentPage, size, localDateScope,upShelfReceipt);
		return RespBean.success("获取成功", respPageBean);
	}

}
