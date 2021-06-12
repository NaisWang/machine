package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.RoleUrlMethod;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-27
 */
public interface IRoleUrlMethodService extends IService<RoleUrlMethod> {

	/**
	 * 获取RoleUrlMethod所有字段数据
	 */
	List<RoleUrlMethod> getRoleUrlMethodList();

}
