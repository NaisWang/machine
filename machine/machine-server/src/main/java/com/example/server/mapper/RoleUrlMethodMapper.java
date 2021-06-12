package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.pojo.RoleUrlMethod;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-27
 */
public interface RoleUrlMethodMapper extends BaseMapper<RoleUrlMethod> {

	/**
	 * 获取RoleUrlMethod所有字段数据
	 */
	List<RoleUrlMethod> getRoleUrlMethodList();
}
