package com.example.server.service;

import com.example.server.pojo.MachineFixBill;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
public interface IMachineFixBillService extends IService<MachineFixBill> {

	/**
	 * 获取账单
	 */
	RespPageBean getMachineFixBill(Integer currentPage, Integer size);
}
