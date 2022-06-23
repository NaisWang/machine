package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.example.server.pojo.MachineFixBill;
import com.example.server.service.impl.MachineFixBillServiceImpl;
import com.example.server.utils.RespBean;
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
@RequestMapping("/machine/fix-bill")
public class MachineFixBillController {
	@Autowired
	private MachineFixBillServiceImpl machineFixBillService;

	@ApiOperation("获取维修账单")
	@GetMapping("/")
	public RespBean getMachineFixBill(@RequestParam(defaultValue = "1") Integer currentPage,
																		@RequestParam(defaultValue = "10") Integer size) {
		return RespBean.success("获取成功", machineFixBillService.getMachineFixBill(currentPage, size));
	}

	@ApiOperation("修改维修账单")
	@PutMapping("/")
	public RespBean modifyMachineFixBill(MachineFixBill machineFixBill) {
		System.out.println("fff");
		System.out.println(machineFixBill);
		if (machineFixBillService.updateById(machineFixBill)) {
			return RespBean.success("修改成功");
		}
		return RespBean.success("修改失败");
	}


}
