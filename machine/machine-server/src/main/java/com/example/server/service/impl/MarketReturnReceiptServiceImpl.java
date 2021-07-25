package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.MarketReturnReceipt;
import com.example.server.mapper.MarketReturnReceiptMapper;
import com.example.server.service.IMarketReturnReceiptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-12
 */
@Service
public class MarketReturnReceiptServiceImpl extends ServiceImpl<MarketReturnReceiptMapper, MarketReturnReceipt> implements IMarketReturnReceiptService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取销售退货订单
	 */
	@Override
	public RespPageBean getMarketReturnReceipt(Integer currentPage, Integer size, MarketReturnReceipt marketReturnReceipt) {
		Page<MarketReturnReceipt> marketReturnReceiptPage = new Page<>(currentPage, size);
		IPage<MarketReturnReceipt> iPage = machineMapper.getMarketReturnReceipt(marketReturnReceiptPage, marketReturnReceipt);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
