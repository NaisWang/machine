package com.example.server.service;

import com.example.server.pojo.SubStorageLocation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-28
 */
public interface ISubStorageLocationService extends IService<SubStorageLocation> {

	/**
	 * 获取子库
	 */
	List<SubStorageLocation> getGetSubStorageLocation(Integer parentStorageLocationId);
}
