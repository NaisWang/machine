package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.mapper.EnterStorageReceiptMapper;
import com.example.server.pojo.Machine;
import com.example.server.service.IEnterStorageReceiptService;
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
 * @since 2021-07-09
 */
@Service
public class EnterStorageReceiptServiceImpl extends ServiceImpl<EnterStorageReceiptMapper, EnterStorageReceipt> implements IEnterStorageReceiptService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取入库单信息
	 */
	@Override
	public RespPageBean getEnterStorageReceipt(Integer currentPage, Integer size, EnterStorageReceipt enterStorageReceipt) {
		Page<EnterStorageReceipt> pages = new Page<>(currentPage, size);
		IPage<EnterStorageReceipt> iPages = machineMapper.getEnterStorageReceipt(pages, enterStorageReceipt);
		RespPageBean respPageBean = new RespPageBean(iPages.getTotal(), iPages.getRecords());
		return respPageBean;
	}
}
