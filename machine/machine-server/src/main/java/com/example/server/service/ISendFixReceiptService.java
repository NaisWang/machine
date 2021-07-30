package com.example.server.service;

import com.example.server.pojo.SendFixReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
public interface ISendFixReceiptService extends IService<SendFixReceipt> {

	/**
	 * 获取送外修单
	 */
	RespPageBean getSendFixReceipt(Integer currentPage, Integer size, SendFixReceipt sendFixReceipt);
}
