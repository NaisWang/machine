package com.example.server.service;

import com.example.server.pojo.MachineRecall;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-30
 */
public interface IMachineRecallService extends IService<MachineRecall> {

	/**
	 * 获取机器召回信息
	 */
	RespPageBean getMachineRecall(Integer currentPage, Integer size, MachineRecall machineRecall, LocalDate[] bidDateScope);
}
