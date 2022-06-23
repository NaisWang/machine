package com.example.server.service;

import com.example.server.pojo.PurchaseReturnReceiptToMachine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-09-14
 */
public interface IPurchaseReturnReceiptToMachineService extends IService<PurchaseReturnReceiptToMachine> {

	/**
	 * 获取采购退货单中的机器
	 */
	RespPageBean getPurchaseReturnReceiptToMachine(Integer currentPage, Integer size, Integer receiptId);
}
