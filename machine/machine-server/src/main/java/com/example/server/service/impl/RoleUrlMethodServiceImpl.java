package com.example.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.mapper.RoleUrlMethodMapper;
import com.example.server.pojo.RoleUrlMethod;
import com.example.server.service.IRoleUrlMethodService;
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
public class RoleUrlMethodServiceImpl extends ServiceImpl<RoleUrlMethodMapper, RoleUrlMethod> implements IRoleUrlMethodService {

	@Autowired
	private RoleUrlMethodMapper roleUrlMethodMapper;

	/**
	 * 获取RoleUrlMethod所有字段数据
	 */
	@Override
	public List<RoleUrlMethod> getRoleUrlMethodList() {
		return roleUrlMethodMapper.getRoleUrlMethodList();
	}
}
