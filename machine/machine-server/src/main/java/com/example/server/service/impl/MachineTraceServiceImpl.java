package com.example.server.service.impl;

import com.example.server.mapper.MachineRecallMapper;
import com.example.server.pojo.MachineTrace;
import com.example.server.mapper.MachineTraceMapper;
import com.example.server.pojo.Statistics;
import com.example.server.service.IMachineTraceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-27
 */
@Service
public class MachineTraceServiceImpl extends ServiceImpl<MachineTraceMapper, MachineTrace> implements IMachineTraceService {

	@Autowired
	private MachineTraceMapper machineTraceMapper;

	/**
	 * 获取所有数据
	 */
	@Override
	public List<Statistics> getAllStatistics(Integer[] empIds, Integer[] statusIds, Integer[] dateScope) {
		return machineTraceMapper.getAllStatistics(empIds, statusIds, dateScope);
	}
}
