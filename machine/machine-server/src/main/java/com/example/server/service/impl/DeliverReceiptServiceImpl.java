package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.DeliverReceipt;
import com.example.server.mapper.DeliverReceiptMapper;
import com.example.server.service.IDeliverReceiptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class DeliverReceiptServiceImpl extends ServiceImpl<DeliverReceiptMapper, DeliverReceipt> implements IDeliverReceiptService {

	@Autowired
	private DeliverReceiptMapper deliverReceiptMapper;

	/**
	 * 分页获取转交表数据
	 */
	@Override
	public IPage<DeliverReceipt> getDeliverReceiptByPage(Integer currentPage, Integer size, DeliverReceipt deliverReceipt, LocalDate[] localDateScope, Integer empId) {
		Page<DeliverReceipt> page = new Page<>(currentPage, size);
		IPage<DeliverReceipt> iPage = deliverReceiptMapper.getDeliverReceipt(page, deliverReceipt, localDateScope, empId);
		return iPage;
	}
}
