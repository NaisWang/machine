package com.example.server.service;

import com.example.server.pojo.PurchaseOrderModify;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whz
 * @since 2021-05-29
 */
public interface IPurchaseOrderModifyService extends IService<PurchaseOrderModify> {

	/**
	 * 判断当前用户能修改采购单中哪些字段
	 */
	PurchaseOrderModify isModifyFiled(String substring);

}
