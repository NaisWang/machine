package com.example.server.mapper;

import com.example.server.pojo.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface RoleMapper extends BaseMapper<Role> {

	/**
	 * 根据用户id查其角色
	 */
	List<Role> getRolesByEmployeeId(Integer id);

	/**
	 * 获取所有角色以及对应的url
	 */
	List<Role> getUrlsWithRole();
}
