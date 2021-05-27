package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.Employee;
import com.example.server.mapper.EmployeeMapper;
import com.example.server.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
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
	public Employee getEmployeeByUsername(String username){
		return employeeMapper.selectOne(new QueryWrapper<Employee>().eq("username", username));
	}

}
