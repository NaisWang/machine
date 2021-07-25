package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.Machine;
import com.example.server.pojo.PurchaseReturnReceipt;
import com.example.server.mapper.PurchaseReturnReceiptMapper;
import com.example.server.service.IPurchaseReturnReceiptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-11
 */
@Service
public class PurchaseReturnReceiptServiceImpl extends ServiceImpl<PurchaseReturnReceiptMapper, PurchaseReturnReceipt> implements IPurchaseReturnReceiptService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取所有采购退货单据
	 */
	@Override
	public RespPageBean getPurchaseReturnReceipt(Integer currentPage, Integer size, PurchaseReturnReceipt purchaseReturnReceipt) {
		Page<PurchaseReturnReceipt> purchaseReturnReceiptPage = new Page<>(currentPage, size);
		IPage<PurchaseReturnReceipt> iPage = machineMapper.getPurchaseReturnReceipt(purchaseReturnReceiptPage, purchaseReturnReceipt);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
