package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.StorageLocation;
import com.example.server.service.impl.StorageLocationServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-26
 */
@RestController
@RequestMapping("/storage-location")
public class StorageLocationController {

	@Autowired
	private StorageLocationServiceImpl storageLocationService;

	@ApiModelProperty("获取库位")
	@GetMapping("/")
	public RespBean getStorageLocation() {
		List<StorageLocation> storageLocationList = storageLocationService.getStorageLocation();
		return RespBean.success("获取成功", storageLocationList);
	}

	@ApiModelProperty("添加库位")
	@PostMapping("/")
	public RespBean addStorageLocation(@RequestBody StorageLocation storageLocation) {
		System.out.println(storageLocation);
		if (storageLocationService.save(storageLocation)) {
			return RespBean.success("添加成功");
		}
		return RespBean.success("添加失败");
	}

	@ApiModelProperty("修改库位")
	@PutMapping("/")
	public RespBean modifyStorageLocation(@RequestBody StorageLocation storageLocation) {
		if (storageLocationService.update(storageLocation, new QueryWrapper<StorageLocation>().eq("id", storageLocation.getId()))) {
			return RespBean.success("添加成功");
		}
		return RespBean.success("添加失败");
	}


}
