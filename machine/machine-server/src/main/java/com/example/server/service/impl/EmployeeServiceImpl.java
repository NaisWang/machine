package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.mapper.EmployeeRoleMapper;
import com.example.server.pojo.Employee;
import com.example.server.mapper.EmployeeMapper;
import com.example.server.pojo.EmployeeRole;
import com.example.server.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;

	/**
	 * 通过用户名获取employee
	 */
	public Employee getEmployeeByUsername(String username) {
		return employeeMapper.selectOne(new QueryWrapper<Employee>().eq("username", username));
	}

	/**
	 * 获取所有员工
	 *
	 * @return
	 */
	@Override
	public List<Employee> getAllEmployee(Employee employee) {
		return employeeMapper.getAllEmployee(employee);
	}


	/**
	 * 获取员工Corr
	 *
	 * @return
	 */
	@Override
	public List<Employee> getEmpCorr() {
		return employeeMapper.selectList(new QueryWrapper<Employee>().select("id", "name"));
	}

}
