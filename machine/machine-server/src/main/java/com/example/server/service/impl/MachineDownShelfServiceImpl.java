package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MachineDownShelf;
import com.example.server.mapper.MachineDownShelfMapper;
import com.example.server.service.IMachineDownShelfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-08-19
 */
@Service
public class MachineDownShelfServiceImpl extends ServiceImpl<MachineDownShelfMapper, MachineDownShelf> implements IMachineDownShelfService {

	@Autowired
	private MachineDownShelfMapper machineDownShelfMapper;

	/**
	 * 获取下架信息
	 */
	@Override
	public RespPageBean getMachineDownShelf(Integer currentPage, Integer size, MachineDownShelf machineDownShelf, LocalDate[] bidDateScope) {
		Page<MachineDownShelf> page = new Page<>(currentPage, size);
		IPage<MachineDownShelf> iPage = machineDownShelfMapper.getMachineDownShelf(page, machineDownShelf, bidDateScope);
		return new RespPageBean(iPage.getTotal(), iPage.getRecords());
	}

}
