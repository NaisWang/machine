package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MachineFixBill;
import com.example.server.mapper.MachineFixBillMapper;
import com.example.server.service.IMachineFixBillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
@Service
public class MachineFixBillServiceImpl extends ServiceImpl<MachineFixBillMapper, MachineFixBill> implements IMachineFixBillService {

	@Autowired
	private MachineFixBillMapper machineFixBillMapper;

	/**
	 * 获取账单
	 */
	@Override
	public RespPageBean getMachineFixBill(Integer currentPage, Integer size) {
		Page<MachineFixBill> machineFixBillPage = new Page<>(currentPage, size);
		IPage<MachineFixBill> machineFixBillIPage = machineFixBillMapper.getMachineFixBill(machineFixBillPage);
		return new RespPageBean(machineFixBillIPage.getTotal(), machineFixBillIPage.getRecords());
	}
}
