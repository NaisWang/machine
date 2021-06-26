package com.example.server.controller;


import com.example.server.mapper.PaijiModelMapper;
import com.example.server.pojo.PaijiModel;
import com.example.server.service.IPaijiModelService;
import com.example.server.service.impl.PaijiModelServiceImpl;
import com.example.server.utils.RespBean;
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
 * @since 2021-06-23
 */
@RestController
@RequestMapping("machine/price/model-contrast")
public class PaijiModelController {

	@Autowired
	private PaijiModelServiceImpl paijiModelService;


	@ApiOperation("获取机型对照表")
	@GetMapping("/")
	public RespBean getModelContrast() {
		return RespBean.success("获取成功", paijiModelService.list(null));
	}

	@ApiOperation("更新机型对照表")
	@PostMapping("/")
	@Transactional
	public RespBean updateModelContrast(@RequestBody List<PaijiModel> paijiModels) {
		paijiModelService.truncate();
		try {
			if (paijiModelService.saveBatch(paijiModels)) {
				return RespBean.success("更新成功");
			}
		} catch (Exception e) {
			throw new RuntimeException("更新失败");
		}
		return RespBean.error("更新失败");
	}

	@ApiOperation("删除机型对照表")
	@DeleteMapping("/")
	public RespBean deleteModelContrast(Integer id) {
		if (paijiModelService.removeById(id)) {
			return RespBean.success("删除成功");
		}
		return RespBean.error("删除失败");
	}
}
