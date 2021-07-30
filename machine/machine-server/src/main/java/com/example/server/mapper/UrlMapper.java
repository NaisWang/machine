package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.pojo.Role;
import com.example.server.pojo.Url;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-27
 */
public interface UrlMapper extends BaseMapper<Url> {

	/**
	 * 获取所有Url以及对应的角色英文名
	 */
	List<Url> getUrlsWithRole();

	/**
	 * 根据url获取用户角色
	 */
//	List<Role> getRolesByUrl(@Param("url") String url, @Param("method") Integer method);
	String getRolesByUrl(@Param("url") String url, @Param("method") Integer method);

}
