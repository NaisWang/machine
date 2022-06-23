package com.example.server.service;

import com.example.server.pojo.MarketReturnReceiptToMachine;
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
public interface IMarketReturnReceiptToMachineService extends IService<MarketReturnReceiptToMachine> {

	/**
	 * 获取销售退货单中的机器
	 */
	RespPageBean getMarketReturnReceiptToMachine(Integer currentPage, Integer size, Integer receiptId);
}
