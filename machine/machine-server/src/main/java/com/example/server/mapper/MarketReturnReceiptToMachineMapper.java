package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MarketReturnReceiptToMachine;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
public interface MarketReturnReceiptToMachineMapper extends BaseMapper<MarketReturnReceiptToMachine> {

	/**
	 * 获取销售退货单中的机器
	 */
	IPage<MarketReturnReceiptToMachine> getMarketReturnReceiptToMachine(Page<MarketReturnReceiptToMachine> page, @Param("receiptId") Integer receiptId);
}
