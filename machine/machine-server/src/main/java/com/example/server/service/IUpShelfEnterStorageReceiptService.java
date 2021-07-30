package com.example.server.service;

import com.example.server.pojo.UpShelfEnterStorageReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
public interface IUpShelfEnterStorageReceiptService extends IService<UpShelfEnterStorageReceipt> {

	/**
	 * 获取上架入库单据
	 */
	RespPageBean getUpShelfEnterStorageReceipt(Integer currentPage, Integer size, UpShelfEnterStorageReceipt upShelfEnterStorageReceipt);
}
