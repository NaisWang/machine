package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.DeliverReceipt;
import com.example.server.mapper.DeliverReceiptMapper;
import com.example.server.service.IDeliverReceiptService;
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
public class DeliverReceiptServiceImpl extends ServiceImpl<DeliverReceiptMapper, DeliverReceipt> implements IDeliverReceiptService {

	@Autowired
	private DeliverReceiptMapper deliverReceiptMapper;

	/**
	 * 分页获取转交表数据
	 */
	@Override
	public IPage<DeliverReceipt> getDeliverReceiptByPage(Integer currentPage, Integer size, DeliverReceipt deliverReceipt, LocalDate[] localDateScope) {
		Page<DeliverReceipt> page = new Page<>(currentPage, size);
		IPage<DeliverReceipt> iPage = deliverReceiptMapper.getDeliverReceipt(page, deliverReceipt, localDateScope);
		return iPage;
	}


	/**
	 * 获取需要接收的转交单
	 */
	@Override
	public RespPageBean getReceiveDeliverReceipt(Integer currentPage, Integer size, DeliverReceipt deliverReceipt, Integer empId) {
		Page<DeliverReceipt> deliverReceiptPage = new Page<>(currentPage, size);
		IPage<DeliverReceipt> iPage = deliverReceiptMapper.getReceiveDeliverReceipt(deliverReceiptPage, deliverReceipt, empId);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
