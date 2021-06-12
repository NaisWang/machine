package com.example.server.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.PurchaseOrder;
import com.example.server.pojo.Stock;
import com.example.server.service.impl.PurchaseOrderServiceImpl;
import com.example.server.service.impl.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.desktop.QuitEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author : whz
 */
@Component
public class MachineUtil {

	@Autowired
	private PurchaseOrderServiceImpl purchaseOrderService;
	@Autowired
	private StockServiceImpl stockService;

	private static PurchaseOrderServiceImpl staticPurchaseOrderService;
	private static StockServiceImpl staticStockService;

	@PostConstruct
	public void init() {
		staticPurchaseOrderService = purchaseOrderService;
		staticStockService = stockService;
	}

	/**
	 * 判断采货单中是否有该机器
	 */
	public static int judgePurchaseHaveMachine(PurchaseOrder purchaseOrder) {
		System.out.println(staticPurchaseOrderService.count(new QueryWrapper<PurchaseOrder>().eq("number", purchaseOrder.getNumber()).or().eq("imei", purchaseOrder.getImei())) != 0);
		if (staticPurchaseOrderService.count(new QueryWrapper<PurchaseOrder>().eq("number", purchaseOrder.getNumber())) != 0) {
			return 1;
		}
		if (staticPurchaseOrderService.count(new QueryWrapper<PurchaseOrder>().eq("imei", purchaseOrder.getImei())) != 0) {
			return 2;
		}
		return 0;
	}

	/**
	 * 判断库存中是否有该机器
	 */
	public static int judgeStorageHaveMachine(PurchaseOrder purchaseOrder) {
		if (staticStockService.count(new QueryWrapper<Stock>().eq("number", purchaseOrder.getNumber())) != 0) {
			return 1;
		}
		if (staticStockService.count(new QueryWrapper<Stock>().eq("imei", purchaseOrder.getImei())) != 0) {
			return 2;
		}
		return 0;
	}

}
