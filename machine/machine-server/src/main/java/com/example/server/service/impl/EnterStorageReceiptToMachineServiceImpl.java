package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.EnterStorageReceiptToMachine;
import com.example.server.mapper.EnterStorageReceiptToMachineMapper;
import com.example.server.service.IEnterStorageReceiptToMachineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-08-17
 */
@Service
public class EnterStorageReceiptToMachineServiceImpl extends ServiceImpl<EnterStorageReceiptToMachineMapper, EnterStorageReceiptToMachine> implements IEnterStorageReceiptToMachineService {

	@Autowired
	private EnterStorageReceiptToMachineMapper enterStorageReceiptToMachineMapper;

	/**
	 * 获取入库单中的机器
	 */
	@Override
	public RespPageBean getEnterStorageReceiptToMachine(Integer currentPage, Integer size, Integer receiptId) {
		Page<EnterStorageReceiptToMachine> enterStorageReceiptToMachinePage = new Page<>(currentPage, size);
		IPage<EnterStorageReceiptToMachine> enterStorageReceiptToMachineIPage = enterStorageReceiptToMachineMapper.getEnterStorageReceiptToMachine(enterStorageReceiptToMachinePage, receiptId);
		return new RespPageBean(enterStorageReceiptToMachineIPage.getTotal(), enterStorageReceiptToMachineIPage.getRecords());
	}
}
