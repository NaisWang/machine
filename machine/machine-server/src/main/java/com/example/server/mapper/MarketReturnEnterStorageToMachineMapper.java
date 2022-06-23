package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MarketReturnEnterStorageToMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-09-16
 */
public interface MarketReturnEnterStorageToMachineMapper extends BaseMapper<MarketReturnEnterStorageToMachine> {

	/**
	 * 获取销退入库中的机器
	 */
	IPage<MarketReturnEnterStorageToMachine> getMarketReturnEnterStorageReceipt(Page<MarketReturnEnterStorageToMachine> page, @Param("receiptId") Integer receiptId);
}
