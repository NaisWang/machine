package com.example.server.service.impl;

import com.example.server.pojo.SubStorageLocation;
import com.example.server.mapper.SubStorageLocationMapper;
import com.example.server.service.ISubStorageLocationService;
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
 * @since 2021-07-28
 */
@Service
public class SubStorageLocationServiceImpl extends ServiceImpl<SubStorageLocationMapper, SubStorageLocation> implements ISubStorageLocationService {

	@Autowired
	private SubStorageLocationMapper subStorageLocationMapper;

	/**
	 * 获取子库
	 */
	@Override
	public List<SubStorageLocation> getGetSubStorageLocation(Integer parentStorageLocationId) {
		return subStorageLocationMapper.getGetSubStorageLocation(parentStorageLocationId);
	}
}
