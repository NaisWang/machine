package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.Channel;
import com.example.server.mapper.ChannelMapper;
import com.example.server.service.IChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements IChannelService {

	@Autowired
	private ChannelMapper channelMapper;

	/**
	 * 获取渠道以及钱
	 */
	@Override
	public RespPageBean getChannelAndMoney(Integer currentPage, Integer sizes, Channel channel) {
		Page<Channel> page = new Page<>(currentPage, sizes);
		IPage<Channel> iPage = channelMapper.getChannelAndMoney(page, channel);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
