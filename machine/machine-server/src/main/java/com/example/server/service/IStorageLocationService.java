package com.example.server.service;

import com.example.server.pojo.StorageLocation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-26
 */
public interface IStorageLocationService extends IService<StorageLocation> {

	/**
	 * 获取所有库位
	 */
	List<StorageLocation> getStorageLocation();
}
