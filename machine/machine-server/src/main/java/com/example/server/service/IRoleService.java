package com.example.server.service;

import com.example.server.pojo.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface IRoleService extends IService<Role> {

	/**
	 * 通过员工id获取roles
	 */
	List<Role> getRolesByEmployeeId(Integer id);

}
