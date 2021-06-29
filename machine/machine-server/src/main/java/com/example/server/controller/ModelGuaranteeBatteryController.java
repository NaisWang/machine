package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.ModelGuaranteeBattery;
import com.example.server.service.impl.ModelGuaranteeBatteryServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-06-24
 */
@RestController
@RequestMapping("/machine/price/model-guarantee-battery")
public class ModelGuaranteeBatteryController {

	@Autowired
	private ModelGuaranteeBatteryServiceImpl modelGuaranteeBatteryService;

	@ApiOperation("得到机型-保修-电池对应表")
	@GetMapping("/")
	public RespBean getModelGuaranteeBattery() {
		List<ModelGuaranteeBattery> modelGuaranteeBatteryList = modelGuaranteeBatteryService.list(null);
		if (modelGuaranteeBatteryList != null) {
			return RespBean.success("查询成功", modelGuaranteeBatteryList);
		}
		return RespBean.error("查询失败");
	}

	@ApiOperation("更新机型-保修-电池对应表")
	@PostMapping("/")
	@Transactional
	public RespBean updateModelGuaranteeBattery(@RequestBody List<ModelGuaranteeBattery> modelGuaranteeBatteryList) {
		try {
			modelGuaranteeBatteryService.remove(new QueryWrapper<>());
			modelGuaranteeBatteryService.saveBatch(modelGuaranteeBatteryList);
			return RespBean.success("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("更新失败");
		}
	}

}
