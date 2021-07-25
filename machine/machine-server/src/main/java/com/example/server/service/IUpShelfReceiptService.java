package com.example.server.service;

import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.UpShelfReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-17
 */
public interface IUpShelfReceiptService extends IService<UpShelfReceipt> {

	/**
	 * 获取上架单据
	 */
	RespPageBean getUpShelfReceipt(Integer currentPage, Integer size, LocalDate[] localDateScope , UpShelfReceipt upShelfReceipt);
}
