package com.example.server.service;

import com.example.server.pojo.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
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


	/**
	 * 获取所有员工
	 *
	 * @return
	 */
	List<Employee> getAllEmployee(Employee employee);

	/**
	 * 获取员工Corr
	 *
	 * @return
	 */
	List<Employee> getEmpCorr();
}
