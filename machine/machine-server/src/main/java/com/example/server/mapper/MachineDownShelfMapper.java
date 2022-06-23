package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MachineDownShelf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDate;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-08-19
 */
public interface MachineDownShelfMapper extends BaseMapper<MachineDownShelf> {

	/**
	 * 获取下架信息
	 */
	IPage<MachineDownShelf> getMachineDownShelf(Page<MachineDownShelf> page, MachineDownShelf machineDownShelf, LocalDate[] bidDateScope);
}
