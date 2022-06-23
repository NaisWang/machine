package com.example.server.service;

import com.example.server.pojo.MarketOrderReceiptToMachine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
public interface IMarketOrderReceiptToMachineService extends IService<MarketOrderReceiptToMachine> {

	/**
	 * 获取销售订单中的机器
	 */
	RespPageBean getMarketOrderReceiptToMachine(Integer currentPage, Integer size, Integer receiptId);
}
