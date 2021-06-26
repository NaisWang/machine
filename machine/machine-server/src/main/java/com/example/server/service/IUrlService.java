package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.Role;
import com.example.server.pojo.Url;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-27
 */
public interface IUrlService extends IService<Url> {

	/**
	 * 获取所有角色以及对应的url
	 */
	public List<Url> getUrlsWithRole();

	/**
	 * 根据url获取Role
	 */
	public List<Role> getRolesByUrl(String url, Integer method);
}
