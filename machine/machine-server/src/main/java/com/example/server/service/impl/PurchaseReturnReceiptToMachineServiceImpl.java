package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.PurchaseReturnReceiptToMachine;
import com.example.server.mapper.PurchaseReturnReceiptToMachineMapper;
import com.example.server.service.IPurchaseReturnReceiptToMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-09-14
 */
@Service
public class PurchaseReturnReceiptToMachineServiceImpl extends ServiceImpl<PurchaseReturnReceiptToMachineMapper, PurchaseReturnReceiptToMachine> implements IPurchaseReturnReceiptToMachineService {

	@Autowired
	private PurchaseReturnReceiptToMachineMapper purchaseReturnReceiptToMachineMapper;

	/**
	 * 获取采购退货单中的机器
	 */
	@Override
	public RespPageBean getPurchaseReturnReceiptToMachine(Integer currentPage, Integer size, Integer receiptId) {
		Page<PurchaseReturnReceiptToMachine> page = new Page<>(currentPage, size);
		IPage<PurchaseReturnReceiptToMachine> iPage = purchaseReturnReceiptToMachineMapper.getPurchaseReturnReceiptToMachine(page, receiptId);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
