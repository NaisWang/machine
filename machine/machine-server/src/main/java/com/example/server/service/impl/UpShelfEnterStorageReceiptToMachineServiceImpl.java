package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.UpShelfEnterStorageReceiptToMachine;
import com.example.server.mapper.UpShelfEnterStorageReceiptToMachineMapper;
import com.example.server.service.IUpShelfEnterStorageReceiptToMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-09-16
 */
@Service
public class UpShelfEnterStorageReceiptToMachineServiceImpl extends ServiceImpl<UpShelfEnterStorageReceiptToMachineMapper, UpShelfEnterStorageReceiptToMachine> implements IUpShelfEnterStorageReceiptToMachineService {

	@Autowired
	private UpShelfEnterStorageReceiptToMachineMapper upShelfEnterStorageReceiptToMachineMapper;

	/**
	 * 获取上架入库单中的机器
	 */
	@Override
	public RespPageBean getUpShelfEnterStorageReceiptToMachine(Integer currentPage, Integer size, Integer receiptId) {
		Page<UpShelfEnterStorageReceiptToMachine>	page = new Page<>(currentPage, size);
		IPage<UpShelfEnterStorageReceiptToMachine> iPage = upShelfEnterStorageReceiptToMachineMapper.getUpShelfEnterStorageReceiptToMachine(page, receiptId);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
