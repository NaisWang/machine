package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.Log;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-16
 */
public interface LogMapper extends BaseMapper<Log> {

	/**
	 * 获取操作日志信息
	 */
	IPage<Log> getLog(Page<Log> page, @Param("log") Log log, @Param("localDateTimes") LocalDate[] localDates);
}
