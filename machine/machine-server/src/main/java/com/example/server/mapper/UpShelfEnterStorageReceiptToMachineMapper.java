package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.UpShelfEnterStorageReceiptToMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-09-16
 */
public interface UpShelfEnterStorageReceiptToMachineMapper extends BaseMapper<UpShelfEnterStorageReceiptToMachine> {

	/**
	 * 获取上架入库单中机器
	 */
	IPage<UpShelfEnterStorageReceiptToMachine> getUpShelfEnterStorageReceiptToMachine(Page<UpShelfEnterStorageReceiptToMachine> page, @Param("receiptId") Integer receiptId);
}
