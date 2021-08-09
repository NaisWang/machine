package com.example.server.service;

import com.example.server.pojo.OperateTrace;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.Statistics;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-30
 */
public interface IOperateTraceService extends IService<OperateTrace> {

	/**
	 * 获取操作追踪
	 */
	RespPageBean getOperateTrace(Integer currentPage, Integer size, OperateTrace operateTrace, LocalDate[] bidDateScope);

	/**
	 * 获取统计数据
	 */
	List<Statistics> getStatistics(Integer id, Integer type);
}
