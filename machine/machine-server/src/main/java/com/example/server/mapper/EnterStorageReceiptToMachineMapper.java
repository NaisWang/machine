package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.EnterStorageReceiptToMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-08-17
 */
public interface EnterStorageReceiptToMachineMapper extends BaseMapper<EnterStorageReceiptToMachine> {

	/**
	 * 获取入库单中的机器
	 */
	IPage<EnterStorageReceiptToMachine> getEnterStorageReceiptToMachine(Page<EnterStorageReceiptToMachine> enterStorageReceiptToMachinePage, @Param("receiptId") Integer receiptId);
}
