package com.example.server.service;

import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.Machine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.PurchaseOrderReceipt;
import com.example.server.pojo.PurchaseReturnReceipt;
import com.example.server.utils.RespPageBean;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-07
 */
public interface IMachineService extends IService<Machine> {

	/**
	 * 一键入库
	 */
	Boolean updateEnterStorageReceiptForOneKey(EnterStorageReceipt enterStorageReceipt, Integer purchaseOrderId, LocalDateTime localDateTime, Integer empId);

}
