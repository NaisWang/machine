package com.example.server.service;

import com.example.server.pojo.EnterStorageReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-09
 */
public interface IEnterStorageReceiptService extends IService<EnterStorageReceipt> {

	/**
	 * 获取入库单信息
	 */
	RespPageBean getEnterStorageReceipt(Integer currentPage, Integer size, EnterStorageReceipt enterStorageReceipt);

}
