package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MarketOrderReceiptToMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
public interface MarketOrderReceiptToMachineMapper extends BaseMapper<MarketOrderReceiptToMachine> {

	/**
	 * 获取销售订单中的机器
	 */
	IPage<MarketOrderReceiptToMachine> getMarketOrderReceiptToMachine(Page<MarketOrderReceiptToMachine> page, @Param("receiptId") Integer receiptId);
}
