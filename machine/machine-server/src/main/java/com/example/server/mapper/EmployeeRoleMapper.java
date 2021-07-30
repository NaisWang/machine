package com.example.server.mapper;

import com.example.server.pojo.EmployeeRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface EmployeeRoleMapper extends BaseMapper<EmployeeRole> {

	/**
	 * 更新用户角色
	 */
	Integer addEmpRole(@Param("id") Integer id, @Param("rids") Integer[] rids);
}
