package com.example.server.service;

import com.example.server.pojo.MachineTrace;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.Statistics;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-07-27
 */
public interface IMachineTraceService extends IService<MachineTrace> {

	/**
	 * 获取所有数据
	 */
	List<Statistics> getAllStatistics(Integer[] empIds, Integer[] statusIds, Integer[] dateScope);

	/**
	 * 获取当前登陆者的数据
	 */
	List<Statistics> getOneStatistics(Integer empId);
}
