package com.example.server.service;

import com.example.server.pojo.UpShelfEnterStorageReceiptToMachine;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.utils.RespPageBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2021-09-16
 */
public interface IUpShelfEnterStorageReceiptToMachineService extends IService<UpShelfEnterStorageReceiptToMachine> {

	/**
	 * 获取上架入库单中的机器
	 */
	RespPageBean getUpShelfEnterStorageReceiptToMachine(Integer currentPage, Integer size, Integer recieptId);
}
