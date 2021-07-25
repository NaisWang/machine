package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.PurchaseOrderReceipt;
import com.example.server.mapper.PurchaseOrderReceiptMapper;
import com.example.server.service.IPurchaseOrderReceiptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-08
 */
@Service
public class PurchaseOrderReceiptServiceImpl extends ServiceImpl<PurchaseOrderReceiptMapper, PurchaseOrderReceipt> implements IPurchaseOrderReceiptService {

	@Autowired
	private PurchaseOrderReceiptMapper purchaseOrderReceiptMapper;

	/**
	 * 获取采购单最大订单号
	 */
	@Override
	public Integer getPurchaseOrderReceiptMaxId() {
		return purchaseOrderReceiptMapper.selectOne(new QueryWrapper<PurchaseOrderReceipt>().orderByDesc("purchase_order").last("limit 1")).getPurchaseOrder();
	}
}
