package com.example.server.service;

import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.MarketReturnEnterStorage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
public interface IMarketReturnEnterStorageService extends IService<MarketReturnEnterStorage> {

	/**
	 * 获取销退入库单信息
	 */
	RespPageBean getMarketReturnEnterStorageReceipt(Integer currentPage, Integer size, MarketReturnEnterStorage marketReturnEnterStorage);
}
