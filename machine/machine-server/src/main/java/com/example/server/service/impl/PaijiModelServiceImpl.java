package com.example.server.service.impl;

import com.example.server.mapper.PaijiMapper;
import com.example.server.pojo.PaijiModel;
import com.example.server.mapper.PaijiModelMapper;
import com.example.server.service.IPaijiModelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-06-23
 */
@Service
public class PaijiModelServiceImpl extends ServiceImpl<PaijiModelMapper, PaijiModel> implements IPaijiModelService {

	@Autowired
	private PaijiModelMapper paijiModelMapper;

	/**
	 * 清空表
	 */
	@Override
	public void truncate() {
		paijiModelMapper.truncate();
	}
}
