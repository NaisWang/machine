package com.example.server.service.impl;

import com.example.server.pojo.EmployeeRole;
import com.example.server.mapper.EmployeeRoleMapper;
import com.example.server.service.IEmployeeRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@Service
public class EmployeeRoleServiceImpl extends ServiceImpl<EmployeeRoleMapper, EmployeeRole> implements IEmployeeRoleService {

	@Autowired
	private EmployeeRoleMapper employeeRoleMapper;

	/**
	 * 创建用户角色对应表
	 */
	@Override
	public Integer addEmpRole(Integer id, Integer[] rids) {
		return employeeRoleMapper.addEmpRole(id, rids);
	}
}
