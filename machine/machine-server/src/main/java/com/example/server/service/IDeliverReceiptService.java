package com.example.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.server.pojo.DeliverReceipt;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-22
 */
public interface IDeliverReceiptService extends IService<DeliverReceipt> {

	/**
	 * 分页获取转交表数据
	 */
	IPage<DeliverReceipt> getDeliverReceiptByPage(Integer currentPage, Integer size, DeliverReceipt deliverReceipt, LocalDate[] localDateScope, Integer empId);
}
