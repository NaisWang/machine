package com.example.server.service.impl;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.SendFixReceiptMapper;
import com.example.server.pojo.SendFixMachine;
import com.example.server.mapper.SendFixMachineMapper;
import com.example.server.service.ISendFixMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
@Service
public class SendFixMachineServiceImpl extends ServiceImpl<SendFixMachineMapper, SendFixMachine> implements ISendFixMachineService {

	@Autowired
	private SendFixMachineMapper sendFixMachineMapper;

	/*
		获取送外修中的机器
	 */
	@Override
	public RespPageBean getSendFixMachine(Integer currentPage, Integer size, SendFixMachine sendFixMachine, LocalDate[] localDateScope) {
		Page<SendFixMachine> page = new Page<>(currentPage, size);
		IPage<SendFixMachine> iPage = sendFixMachineMapper.getSendFixMachine(page, sendFixMachine, localDateScope);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
