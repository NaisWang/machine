package com.example.server.service.impl;

import com.example.server.pojo.Menu;
import com.example.server.mapper.MenuMapper;
import com.example.server.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

	@Autowired
	private MenuMapper menuMapper;

	/**
	 * 获取所有菜单，包括子菜单
	 */
	@Override
	public List<Menu> getAllMenuWithChildren(String roleIds) {
		return menuMapper.getAllMenuWithChildren(-1, roleIds);
	}
}
