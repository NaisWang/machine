package com.example.server.service;

import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.MarketOrderReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-11
 */
public interface IMarketOrderReceiptService extends IService<MarketOrderReceipt> {

	/**
	 * 获取销售订单
	 */
	RespPageBean getMarketOrderReceipt(Integer currentPage, Integer size, MarketOrderReceipt marketOrderReceipt);
}
