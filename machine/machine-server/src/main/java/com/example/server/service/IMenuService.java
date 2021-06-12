package com.example.server.service;

import com.example.server.pojo.Menu;
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
public interface IMenuService extends IService<Menu> {

	/**
	 * 获取所有菜单，包括子菜单
	 */
	List<Menu> getAllMenuWithChildren(String roleIds);

}
