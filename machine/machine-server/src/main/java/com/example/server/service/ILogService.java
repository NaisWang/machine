package com.example.server.service;

import com.example.server.pojo.Log;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-16
 */
public interface ILogService extends IService<Log> {

	/**
	 * 获取操作日志信息
	 */
	RespPageBean getLog(Integer currentPage, Integer size, Log log, LocalDate[] localDates);
}
