package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.SendFixReceipt;
import com.example.server.mapper.SendFixReceiptMapper;
import com.example.server.service.ISendFixReceiptService;
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
 * @since 2021-07-25
 */
@Service
public class SendFixReceiptServiceImpl extends ServiceImpl<SendFixReceiptMapper, SendFixReceipt> implements ISendFixReceiptService {

	@Autowired
	private SendFixReceiptMapper sendFixReceiptMapper;

	/**
	 * 获取送外修单
	 */
	@Override
	public RespPageBean getSendFixReceipt(Integer currentPage, Integer size, SendFixReceipt sendFixReceipt) {
		Page<SendFixReceipt> page = new Page<>(currentPage, size);
		IPage<SendFixReceipt> iPage = sendFixReceiptMapper.getSendFixReceipt(page, sendFixReceipt);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}

}
