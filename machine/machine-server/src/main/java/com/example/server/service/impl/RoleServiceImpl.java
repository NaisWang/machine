package com.example.server.service.impl;

import com.example.server.pojo.Role;
import com.example.server.mapper.RoleMapper;
import com.example.server.service.IRoleService;
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
 * @since 2021-05-26
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

	@Autowired
	private RoleMapper roleMapper;

	/**
	 * 通过员工id获取roles
	 */
	@Override
	public List<Role> getRolesByEmployeeId(Integer id) {
		return roleMapper.getRolesByEmployeeId(id);
	}
}
