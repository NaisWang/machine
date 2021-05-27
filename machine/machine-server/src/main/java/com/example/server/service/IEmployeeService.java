package com.example.server.service;

import com.example.server.mapper.EmployeeMapper;
import com.example.server.pojo.Employee;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface IEmployeeService extends IService<Employee> {

	/**
	 * 通过用户名获取employee
	 */
	public Employee getEmployeeByUsername(String username);

}
