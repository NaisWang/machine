package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.OperateTrace;
import com.example.server.mapper.OperateTraceMapper;
import com.example.server.pojo.Statistics;
import com.example.server.service.IOperateTraceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-30
 */
@Service
public class OperateTraceServiceImpl extends ServiceImpl<OperateTraceMapper, OperateTrace> implements IOperateTraceService {

	@Autowired
	private OperateTraceMapper operateTraceMapper;

	/**
	 * 获取操作追踪
	 */
	@Override
	public RespPageBean getOperateTrace(Integer currentPage, Integer size, OperateTrace operateTrace, LocalDate[] bidDateScope) {
		Page<OperateTrace> page = new Page<>(currentPage, size);
		IPage<OperateTrace> iPage = operateTraceMapper.getOperateTrace(page, operateTrace, bidDateScope);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}

	/**
	 * 获取统计数据
	 */
	@Override
	public List<Statistics> getStatistics(Integer id, Integer type) {
		return operateTraceMapper.getStatistics(id, type);
	}
}
