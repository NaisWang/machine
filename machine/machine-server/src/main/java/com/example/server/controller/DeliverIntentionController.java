package com.example.server.controller;


import com.example.server.service.impl.DeliverIntentionServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-22
 */
@RestController
@RequestMapping("/deliver/intention")
public class DeliverIntentionController {

	@Autowired
	private DeliverIntentionServiceImpl deliverIntentionService;

	@ApiOperation("获取转交所有类别")
	@GetMapping("/")
	public RespBean getDeliverIntention() {
		return RespBean.success("获取成功", deliverIntentionService.list(null));
	}

}
