package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MarketReturnReceiptToMachine;
import com.example.server.mapper.MarketReturnReceiptToMachineMapper;
import com.example.server.service.IMarketReturnReceiptToMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-08-27
 */
@Service
public class MarketReturnReceiptToMachineServiceImpl extends ServiceImpl<MarketReturnReceiptToMachineMapper, MarketReturnReceiptToMachine> implements IMarketReturnReceiptToMachineService {

	@Autowired
	private MarketReturnReceiptToMachineMapper marketReturnReceiptToMachineMapper;

	/**
	 * 获取销售退货单中的机器
	 */
	@Override
	public RespPageBean getMarketReturnReceiptToMachine(Integer currentPage, Integer size, Integer receiptId) {
		Page<MarketReturnReceiptToMachine> page = new Page<>(currentPage, size);
		IPage<MarketReturnReceiptToMachine> iPage = marketReturnReceiptToMachineMapper.getMarketReturnReceiptToMachine(page, receiptId);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
