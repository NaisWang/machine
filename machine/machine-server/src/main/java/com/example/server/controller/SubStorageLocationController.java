package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.SubStorageLocation;
import com.example.server.service.impl.SubStorageLocationServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-28
 */
@RestController
@RequestMapping("/sub-storage-location")
public class SubStorageLocationController {

	@Autowired
	private SubStorageLocationServiceImpl subStorageLocationService;

	@ApiOperation("获取所有子库")
	@GetMapping("/")
	public RespBean getSubStorageLocation(Integer parentStorageLocationId) {
		List<SubStorageLocation> subStorageLocations = subStorageLocationService.getGetSubStorageLocation(parentStorageLocationId);
		return RespBean.success("获取成功", subStorageLocations);
	}

	@ApiOperation("添加子库")
	@PostMapping("/")
	public RespBean addSubStorageLocation(@RequestBody SubStorageLocation subStorageLocation) {
		if (subStorageLocationService.save(subStorageLocation)) {
			return RespBean.success("添加成功");
		}
		return RespBean.success("添加失败");
	}

	@ApiOperation("修改子库")
	@PutMapping("/")
	public RespBean modifySubStorageLocation(@RequestBody SubStorageLocation subStorageLocation) {
		if (subStorageLocationService.update(subStorageLocation, new UpdateWrapper<SubStorageLocation>().eq("id", subStorageLocation.getId()).set("gate_emp_id", subStorageLocation.getGateEmpId()))) {
			return RespBean.success("修改成功");
		}
		return RespBean.success("修改失败");
	}

}
