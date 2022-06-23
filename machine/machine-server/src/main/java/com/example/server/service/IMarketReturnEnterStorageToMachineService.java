package com.example.server.service;

import com.example.server.pojo.MarketReturnEnterStorageToMachine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-09-16
 */
public interface IMarketReturnEnterStorageToMachineService extends IService<MarketReturnEnterStorageToMachine> {

	/**
	 * 获取销退入库中的机器
	 */
	RespPageBean getMarketReturnEnterStorageReceipt(Integer currentPage, Integer size, Integer receiptId);
}
