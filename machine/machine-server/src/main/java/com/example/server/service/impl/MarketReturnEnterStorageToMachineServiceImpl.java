package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MarketReturnEnterStorageToMachine;
import com.example.server.mapper.MarketReturnEnterStorageToMachineMapper;
import com.example.server.service.IMarketReturnEnterStorageToMachineService;
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
 * @since 2021-09-16
 */
@Service
public class MarketReturnEnterStorageToMachineServiceImpl extends ServiceImpl<MarketReturnEnterStorageToMachineMapper, MarketReturnEnterStorageToMachine> implements IMarketReturnEnterStorageToMachineService {

	@Autowired
	private MarketReturnEnterStorageToMachineMapper marketReturnEnterStorageToMachineMapper;

	/**
	 * 获取消退入库中的机器
	 */
	@Override
	public RespPageBean getMarketReturnEnterStorageReceipt(Integer currentPage, Integer size, Integer receiptId) {
		Page<MarketReturnEnterStorageToMachine> page = new Page<>(currentPage, size);
		IPage<MarketReturnEnterStorageToMachine> iPage = marketReturnEnterStorageToMachineMapper.getMarketReturnEnterStorageReceipt(page, receiptId);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
