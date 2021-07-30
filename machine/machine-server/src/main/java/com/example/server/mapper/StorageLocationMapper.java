package com.example.server.mapper;

import com.example.server.pojo.StorageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-26
 */
public interface StorageLocationMapper extends BaseMapper<StorageLocation> {

	/**
	 * 获取库位
	 */
	List<StorageLocation> getStorageLocation();
}
