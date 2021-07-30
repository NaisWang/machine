package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.UpShelfEnterStorageReceipt;
import com.example.server.mapper.UpShelfEnterStorageReceiptMapper;
import com.example.server.service.IUpShelfEnterStorageReceiptService;
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
 * @since 2021-07-25
 */
@Service
public class UpShelfEnterStorageReceiptServiceImpl extends ServiceImpl<UpShelfEnterStorageReceiptMapper, UpShelfEnterStorageReceipt> implements IUpShelfEnterStorageReceiptService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取上架入库单据
	 */
	@Override
	public RespPageBean getUpShelfEnterStorageReceipt(Integer currentPage, Integer size, UpShelfEnterStorageReceipt upShelfEnterStorageReceipt) {
		Page<UpShelfEnterStorageReceipt> page = new Page<>(currentPage, size);
		IPage<UpShelfEnterStorageReceipt> iPage  = machineMapper.getUpShelfEnterStorageReceipt(page, upShelfEnterStorageReceipt);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}
}
