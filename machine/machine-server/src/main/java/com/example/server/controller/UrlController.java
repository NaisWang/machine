package com.example.server.controller;


import com.example.server.pojo.Url;
import com.example.server.service.impl.UrlServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-05-27
 */
@RestController
@RequestMapping("/url")
public class UrlController {

	@Autowired
	private UrlServiceImpl urlService;

	@ApiModelProperty("获取所有url")
	@GetMapping("/")
	public RespBean getUrl() {
		return RespBean.success("获取成功", urlService.list(null));
	}

	@ApiModelProperty("更新所有url")
	@PutMapping("/")
	@Transactional
	public RespBean updateURL(@RequestBody Url[] url) {
		try {
			if (urlService.updateBatchById(Arrays.asList(url))) {
				return RespBean.success("更新成功");
			}
			throw new RuntimeException("更新失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("更新失败");
		}
	}

}
