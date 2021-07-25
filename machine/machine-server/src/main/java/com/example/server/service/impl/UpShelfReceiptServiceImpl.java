package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.UpShelfReceipt;
import com.example.server.mapper.UpShelfReceiptMapper;
import com.example.server.service.IUpShelfReceiptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-17
 */
@Service
public class UpShelfReceiptServiceImpl extends ServiceImpl<UpShelfReceiptMapper, UpShelfReceipt> implements IUpShelfReceiptService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取上架单据
	 */
	@Override
	public RespPageBean getUpShelfReceipt(Integer currentPage, Integer size, LocalDate[] localDateScope, UpShelfReceipt upShelfReceipt) {
		Page<UpShelfReceipt> page = new Page<>(currentPage, size);
		IPage<UpShelfReceipt> iPage = machineMapper.getUpShelfReceipt(page, localDateScope, upShelfReceipt);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
