package com.example.server.mapper;

import com.example.server.pojo.Menu;
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
public interface MenuMapper extends BaseMapper<Menu> {

	/**
	 * 获取菜单id值为实参值的所有子菜单
	 */
	List<Menu> getAllMenuWithChildren(@Param("parentId") Integer parentId, @Param("roleIds") String roleIds);

}
