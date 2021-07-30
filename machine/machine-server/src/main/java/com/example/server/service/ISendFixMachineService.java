package com.example.server.service;

import com.example.server.pojo.SendFixMachine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
public interface ISendFixMachineService extends IService<SendFixMachine> {

	/**
	 * 获取送外修单中的机器
	 */
	RespPageBean getSendFixMachine(Integer currentPage, Integer size, SendFixMachine sendFixMachine, LocalDate[] localDateScope);
}
