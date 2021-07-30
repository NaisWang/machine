package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MachineRecall;
import com.example.server.mapper.MachineRecallMapper;
import com.example.server.service.IMachineRecallService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-30
 */
@Service
public class MachineRecallServiceImpl extends ServiceImpl<MachineRecallMapper, MachineRecall> implements IMachineRecallService {

	@Autowired
	private MachineRecallMapper machineRecallMapper;

	/**
	 * 获取召回信息
	 */
	@Override
	public RespPageBean getMachineRecall(Integer currentPage, Integer size, MachineRecall machineRecall, LocalDate[] bidDateScope) {
		Page<MachineRecall> page = new Page<>(currentPage, size);
		IPage<MachineRecall> iPage = machineRecallMapper.getMachineRecall(page,machineRecall, bidDateScope);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
