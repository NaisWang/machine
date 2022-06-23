package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.PurchaseOrderReceiptToMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-08-17
 */
public interface PurchaseOrderReceiptToMachineMapper extends BaseMapper<PurchaseOrderReceiptToMachine> {

	/**
	 * 获取采购订单中的机器
	 */
	IPage<PurchaseOrderReceiptToMachine> getPurchaseOrderReceiptToMachine(Page<PurchaseOrderReceiptToMachine> page, @Param("receiptId") Integer receiptId);
}
