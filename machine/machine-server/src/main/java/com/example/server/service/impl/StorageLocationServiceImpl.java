package com.example.server.service.impl;

import com.example.server.pojo.StorageLocation;
import com.example.server.mapper.StorageLocationMapper;
import com.example.server.service.IStorageLocationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-26
 */
@Service
public class StorageLocationServiceImpl extends ServiceImpl<StorageLocationMapper, StorageLocation> implements IStorageLocationService {

	@Autowired
	private StorageLocationMapper storageLocationMapper;

	/**
	 * 获取所有库位
	 */
	@Override
	public List<StorageLocation> getStorageLocation() {
		return storageLocationMapper.getStorageLocation();
	}
}
