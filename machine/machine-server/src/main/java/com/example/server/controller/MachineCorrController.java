package com.example.server.controller;

import com.example.server.pojo.Channel;
import com.example.server.service.impl.BrandServiceImpl;
import com.example.server.service.impl.CategoryServiceImpl;
import com.example.server.service.impl.ChannelServiceImpl;
import com.example.server.service.impl.MachineStatusServiceImpl;
import com.example.server.utils.Corr;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @author : whz
 */
@RestController
@RequestMapping("/machine/corr")
public class MachineCorrController {

	@Autowired
	private MachineStatusServiceImpl machineStatusService;
	@Autowired
	private BrandServiceImpl brandService;
	@Autowired
	private CategoryServiceImpl categoryService;
	@Autowired
	private ChannelServiceImpl channelService;

	@ApiOperation("获取机器状态对应关系")
	@GetMapping("/status")
	public RespBean getStatusCorr() {
		return RespBean.success("操作成功", machineStatusService.list(null));
	}

	@ApiOperation("获取所有品牌")
	@GetMapping("/brand")
	public RespBean getBrandCorr() {
		return RespBean.success("操作成功", brandService.list(null));
	}

	@ApiOperation("获取所有品类")
	@GetMapping("/category")
	public RespBean getCategoryCorr() {
		return RespBean.success("操作成功", categoryService.list(null));
	}

	@ApiOperation("获取所有渠道")
	@GetMapping("/channel")
	public RespBean getChannelCorr() {
		List<Channel> channelList = channelService.list(null);
		if (Corr.channelCorr == null) {
			Corr.channelCorr = new HashMap<>();
			for (Channel channel : channelList) {
				Corr.channelCorr.put(channel.getId(), channel.getName());
			}
		}
		return RespBean.success("操作成功", channelList);
	}

}
