package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MarketOrderReceiptToMachine;
import com.example.server.mapper.MarketOrderReceiptToMachineMapper;
import com.example.server.service.IMarketOrderReceiptToMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.error.Mark;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
@Service
public class MarketOrderReceiptToMachineServiceImpl extends ServiceImpl<MarketOrderReceiptToMachineMapper, MarketOrderReceiptToMachine> implements IMarketOrderReceiptToMachineService {

	@Autowired
	private MarketOrderReceiptToMachineMapper marketOrderReceiptToMachineMapper;

	/**
	 * 获取销售订单中的机器
	 */
	@Override
	public RespPageBean getMarketOrderReceiptToMachine(Integer currentPage, Integer size, Integer receiptId) {
		Page<MarketOrderReceiptToMachine> page = new Page<>(currentPage, size);
		IPage<MarketOrderReceiptToMachine> iPage = marketOrderReceiptToMachineMapper.getMarketOrderReceiptToMachine(page, receiptId);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
