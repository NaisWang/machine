package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.DeliverMachine;
import com.example.server.mapper.DeliverMachineMapper;
import com.example.server.pojo.PurchaseReturnReceipt;
import com.example.server.service.IDeliverMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-22
 */
@Service
public class DeliverMachineServiceImpl extends ServiceImpl<DeliverMachineMapper, DeliverMachine> implements IDeliverMachineService {

	@Autowired
	private DeliverMachineMapper deliverMachineMapper;

	/**
	 * 分页获取转交单中的详情
	 */
	@Override
	public RespPageBean getDeliverMachine(Integer currentPage, Integer size, DeliverMachine deliverMachine, LocalDate[] localDateScope) {
		Page<DeliverMachine> page = new Page<>(currentPage, size);
		IPage<DeliverMachine> iPage = deliverMachineMapper.getDeliverMachine(page, deliverMachine, localDateScope);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
