package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.PurchaseReturnReceiptToMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-09-14
 */
public interface PurchaseReturnReceiptToMachineMapper extends BaseMapper<PurchaseReturnReceiptToMachine> {

	/**
	 * 获取采购退货单中的机器
	 */
	IPage<PurchaseReturnReceiptToMachine> getPurchaseReturnReceiptToMachine(Page<PurchaseReturnReceiptToMachine> page, @Param("receiptId") Integer receiptId);
}
