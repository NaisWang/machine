package com.example.server.service;

import com.example.server.pojo.PurchaseOrderReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-08
 */
public interface IPurchaseOrderReceiptService extends IService<PurchaseOrderReceipt> {

	/**
	 * 获取采购单最大订单号
	 */
	Integer getPurchaseOrderReceiptMaxId();

}
