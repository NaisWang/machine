package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MachineFixBill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
public interface MachineFixBillMapper extends BaseMapper<MachineFixBill> {

	/**
	 * 获取维修账单
	 */
	IPage<MachineFixBill> getMachineFixBill(Page<MachineFixBill> machineFixBillPage);
}
