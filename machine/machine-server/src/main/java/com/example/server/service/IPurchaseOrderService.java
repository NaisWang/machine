package com.example.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.PurchaseOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface IPurchaseOrderService extends IService<PurchaseOrder> {

	/**
	 * 手动向采购单中添加机器
	 */
	Integer manualAddMachine(PurchaseOrder purchaseOrder);

	/**
	 * 分页获取采购单中的机器数据
	 */
	RespPageBean getMachineByPage(Integer currentPage, Integer size, PurchaseOrder purchaseOrder, LocalDate[] bidDateScope);
}
