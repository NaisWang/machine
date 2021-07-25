package com.example.server.service;

import com.example.server.pojo.MarketReturnReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-12
 */
public interface IMarketReturnReceiptService extends IService<MarketReturnReceipt> {

	/**
	 * 获取销售退货订单
	 */
	RespPageBean getMarketReturnReceipt(Integer currentPage, Integer size, MarketReturnReceipt marketReturnReceipt);
}
