package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.OperateTrace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-30
 */
public interface OperateTraceMapper extends BaseMapper<OperateTrace> {

	/**
	 * 获取操作追踪
	 */
	IPage<OperateTrace> getOperateTrace(Page<OperateTrace> page, @Param("operateTrace") OperateTrace operateTrace, @Param("bidDateScope") LocalDate[] bidDateScope);
}
