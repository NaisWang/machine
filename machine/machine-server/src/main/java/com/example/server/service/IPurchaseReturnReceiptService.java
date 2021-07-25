package com.example.server.service;

import com.example.server.pojo.PurchaseReturnReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-11
 */
public interface IPurchaseReturnReceiptService extends IService<PurchaseReturnReceipt> {

	/**
	 * 获取所有采购退货单号
	 */
	RespPageBean getPurchaseReturnReceipt(Integer currentPage, Integer size, PurchaseReturnReceipt purchaseReturnReceipt);
}
