package com.example.server.controller;

import com.example.server.pojo.Channel;
import com.example.server.service.impl.ChannelServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author : whz
 */
@RestController
@RequestMapping("/system/channel")
public class ChannelController {

	@Autowired
	private ChannelServiceImpl channelService;

	@ApiOperation("获取渠道以及对应的钱")
	@GetMapping("/")
	public RespBean getChannelAndMoney(@RequestParam(defaultValue = "1") Integer currentPage,
																		 @RequestParam(defaultValue = "10") Integer sizes,
																		 Channel channel) {
		RespPageBean respPageBean = channelService.getChannelAndMoney(currentPage, sizes, channel);
		return RespBean.success("获取成功", respPageBean);
	}


	@ApiOperation("添加渠道")
	@PostMapping("/")
	public RespBean addChannel(@RequestBody Channel channel) {
		if (channelService.save(channel)) {
			return RespBean.success("添加成功");
		}
		return RespBean.success("添加失败");
	}

	@ApiOperation("修改渠道")
	@PutMapping("/")
	public RespBean modifyChannel(@RequestBody Channel channel) {
		if (channelService.updateById(channel)) {
			return RespBean.success("修改成功");
		}
		return RespBean.success("修改失败");
	}

	@ApiOperation("删除渠道")
	@DeleteMapping("/")
	public RespBean deleteChannel(Integer channelId) {
		if (channelService.removeById(channelId)) {
			return RespBean.success("删除成功");
		}
		return RespBean.success("删除失败");
	}

}
