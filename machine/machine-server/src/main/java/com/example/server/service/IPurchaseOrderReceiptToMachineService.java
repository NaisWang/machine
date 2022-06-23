package com.example.server.service;

import com.example.server.pojo.PurchaseOrderReceiptToMachine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-08-17
 */
public interface IPurchaseOrderReceiptToMachineService extends IService<PurchaseOrderReceiptToMachine> {

	/**
	 * 获取采购订单中的机器
	 */
	RespPageBean getPurchaseOrderReceiptToMachine(Integer currentPage, Integer size, Integer receiptId);
}
