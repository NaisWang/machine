package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.Channel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface ChannelMapper extends BaseMapper<Channel> {

	/**
	 * 获取渠道以及金钱
	 */
	IPage<Channel> getChannelAndMoney(Page<Channel> page, Channel channel);
}
