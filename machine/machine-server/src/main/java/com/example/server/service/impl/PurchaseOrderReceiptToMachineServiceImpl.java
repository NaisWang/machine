package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.PurchaseOrderReceiptToMachine;
import com.example.server.mapper.PurchaseOrderReceiptToMachineMapper;
import com.example.server.service.IPurchaseOrderReceiptToMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-08-17
 */
@Service
public class PurchaseOrderReceiptToMachineServiceImpl extends ServiceImpl<PurchaseOrderReceiptToMachineMapper, PurchaseOrderReceiptToMachine> implements IPurchaseOrderReceiptToMachineService {

	@Autowired
	private PurchaseOrderReceiptToMachineMapper purchaseOrderReceiptToMachineMapper;


	/**
	 * 获取采购订单中的机器
	 */
	@Override
	public RespPageBean getPurchaseOrderReceiptToMachine(Integer currentPage, Integer size, Integer receiptId) {
		Page<PurchaseOrderReceiptToMachine> page = new Page<>(currentPage, size);
		IPage<PurchaseOrderReceiptToMachine> iPage = purchaseOrderReceiptToMachineMapper.getPurchaseOrderReceiptToMachine(page, receiptId);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
