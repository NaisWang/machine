package com.example.server.service;

import com.example.server.pojo.EmployeeRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface IEmployeeRoleService extends IService<EmployeeRole> {

	/**
	 * 添加用户角色对应关系
	 */
	Integer addEmpRole(Integer id, Integer[] rids);
}
