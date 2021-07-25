package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.MarketOrderReceipt;
import com.example.server.mapper.MarketOrderReceiptMapper;
import com.example.server.service.IMarketOrderReceiptService;
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
 * @since 2021-07-11
 */
@Service
public class MarketOrderReceiptServiceImpl extends ServiceImpl<MarketOrderReceiptMapper, MarketOrderReceipt> implements IMarketOrderReceiptService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取所有销售订单
	 */
	@Override
	public RespPageBean getMarketOrderReceipt(Integer currentPage, Integer size, MarketOrderReceipt marketOrderReceipt) {
		Page<MarketOrderReceipt> page = new Page<>(currentPage, size);
		IPage<MarketOrderReceipt> iPage = machineMapper.getMarketOrderReceipt(page, marketOrderReceipt);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
