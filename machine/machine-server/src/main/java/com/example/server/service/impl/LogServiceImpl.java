package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.Log;
import com.example.server.mapper.LogMapper;
import com.example.server.service.ILogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-16
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

	@Autowired
	private LogMapper logMapper;

	/**
	 * 获取操作日志
	 */
	@Override
	public RespPageBean getLog(Integer currentPage, Integer size, Log log, LocalDate[] localDates) {
		Page<Log> page = new Page<>(currentPage, size);
		IPage<Log> iPage = logMapper.getLog(page, log, localDates);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
