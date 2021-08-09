package com.example.server.mapper;

import com.example.server.pojo.MachineTrace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.pojo.Statistics;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-27
 */
public interface MachineTraceMapper extends BaseMapper<MachineTrace> {

	/**
	 * 获取所有数据
	 */
	List<Statistics> getAllStatistics(@Param("empIds") Integer[] empIds, @Param("statusIds") Integer[] statusIds, @Param("dateScope") Integer[] dateScope);
}
