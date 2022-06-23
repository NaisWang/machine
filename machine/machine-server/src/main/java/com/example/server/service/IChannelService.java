package com.example.server.service;

import com.example.server.pojo.Channel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface IChannelService extends IService<Channel> {

	/**
	 * 获取渠道以及钱
	 */
	RespPageBean getChannelAndMoney(Integer currentPage, Integer sizes, Channel channel);
}
