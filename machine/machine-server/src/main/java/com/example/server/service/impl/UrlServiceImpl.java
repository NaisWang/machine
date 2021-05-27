package com.example.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.mapper.UrlMapper;
import com.example.server.pojo.Role;
import com.example.server.pojo.Url;
import com.example.server.service.IUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-27
 */
@Service
public class UrlServiceImpl extends ServiceImpl<UrlMapper, Url> implements IUrlService {

	@Autowired
	private UrlMapper urlMapper;

	/**
	 * 获取所有Url以及对应的角色英文名
	 * @return
	 */
	@Override
	public List<Url> getUrlsWithRole() {
		return urlMapper.getUrlsWithRole();
	}
}
