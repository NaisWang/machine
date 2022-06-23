package com.example.server.service;

import com.example.server.pojo.EnterStorageReceiptToMachine;
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
public interface IEnterStorageReceiptToMachineService extends IService<EnterStorageReceiptToMachine> {

	/**
	 * 获取入库单中的机器
	 */
	RespPageBean getEnterStorageReceiptToMachine(Integer currentPage, Integer size, Integer receiptId);
}
