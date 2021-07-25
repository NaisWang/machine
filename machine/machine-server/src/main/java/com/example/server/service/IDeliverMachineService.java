package com.example.server.service;

import com.example.server.pojo.DeliverMachine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.DeliverReceipt;
import com.example.server.pojo.PurchaseReturnReceipt;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-22
 */
public interface IDeliverMachineService extends IService<DeliverMachine> {

	/**
	 * 分页获取转交单中的详情
	 */
	RespPageBean getDeliverMachine(Integer currentPage, Integer size, DeliverMachine deliverMachine, LocalDate[] localDateScope);
}
