package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.MarketReturnEnterStorage;
import com.example.server.mapper.MarketReturnEnterStorageMapper;
import com.example.server.service.IMarketReturnEnterStorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
@Service
public class MarketReturnEnterStorageServiceImpl extends ServiceImpl<MarketReturnEnterStorageMapper, MarketReturnEnterStorage> implements IMarketReturnEnterStorageService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取销退入库单信息
	 */
	@Override
	public RespPageBean getMarketReturnEnterStorageReceipt(Integer currentPage,Integer size, MarketReturnEnterStorage marketReturnEnterStorage) {
		Page<MarketReturnEnterStorage> pages = new Page<>(currentPage, size);
		IPage<MarketReturnEnterStorage> iPages = machineMapper.getMarketReturnEnterStorageReceipt(pages, marketReturnEnterStorage);
		RespPageBean respPageBean = new RespPageBean(iPages.getTotal(), iPages.getRecords());
		return respPageBean;
	}
}
