package com.example.server.mapper;

import com.example.server.pojo.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

	/**
	 * 获取所有员工
	 * @return
	 */
	List<Employee> getAllEmployee();
}
