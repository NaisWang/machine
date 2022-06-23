package com.example.server.service;

import com.example.server.pojo.MachineDownShelf;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

import java.time.LocalDate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-08-19
 */
public interface IMachineDownShelfService extends IService<MachineDownShelf> {

	/**
	 * 获取下架信息
	 */
	RespPageBean getMachineDownShelf(Integer currentPage, Integer size, MachineDownShelf machineDownShelf, LocalDate[] bidDateScope);

}
