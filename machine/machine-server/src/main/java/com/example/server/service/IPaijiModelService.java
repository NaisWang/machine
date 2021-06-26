package com.example.server.service;

import com.example.server.pojo.PaijiModel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-06-23
 */
public interface IPaijiModelService extends IService<PaijiModel> {

	/**
	 * 清空表
	 */
	void truncate();

}
