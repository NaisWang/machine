package com.example.server.mapper;

import com.example.server.pojo.SubStorageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-28
 */
public interface SubStorageLocationMapper extends BaseMapper<SubStorageLocation> {

	/**
	 * 获取子库
	 */
	List<SubStorageLocation> getGetSubStorageLocation(@Param("parentStorageLocationId") Integer parentStorageLocationId);
}
