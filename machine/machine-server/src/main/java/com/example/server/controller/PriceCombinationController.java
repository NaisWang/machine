package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.PriceCombination;
import com.example.server.service.ICategoryService;
import com.example.server.service.impl.PriceCombinationServiceImpl;
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
 * @since 2021-06-25
 */
@RestController
@RequestMapping("/machine/price/price-combination")
public class PriceCombinationController {

	@Autowired
	private PriceCombinationServiceImpl priceCombinationService;

	@ApiOperation("得到priceCombination表信息")
	@GetMapping("/")
	public RespBean getPriceCombination() {
		List<PriceCombination> priceCombinations = priceCombinationService.list(null);
		if (priceCombinations != null) {
			return RespBean.success("获取成功", priceCombinations);
		}
		return RespBean.error("获取失败");
	}

	@ApiOperation("更新priceCombination表")
	@PostMapping("/")
	@Transactional
	public RespBean updatePriceCombination(@RequestBody List<PriceCombination> priceCombinations) {
		try {
			priceCombinationService.remove(new QueryWrapper<>());
			priceCombinationService.saveBatch(priceCombinations);
			return RespBean.success("更新成功");
		} catch (Exception e) {
			throw new RuntimeException("更新失败");
		}
	}

}
