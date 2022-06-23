package com.example.server.service.impl;

import com.example.server.mapper.FinanceMapper;
import com.example.server.pojo.Finance;
import com.example.server.service.IFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : whz
 */
@Component
public class FinanceServiceImpl implements IFinanceService {

	@Autowired
	private FinanceMapper financeMapper;

	/**
	 * 获取财务情况
	 */
	@Override
	public Finance getFinance() {
		return financeMapper.getFinance();
	}
}
