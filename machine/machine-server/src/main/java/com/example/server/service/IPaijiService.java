package com.example.server.service;

import com.example.server.pojo.Paiji;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-06-16
 */
public interface IPaijiService extends IService<Paiji> {

	/**
	 * 排序获取所有数据
	 *
	 * @return
	 */
	List<Paiji> listOrder(Paiji paiji);
}
