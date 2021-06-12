package com.example.server.service.impl;

import com.example.server.pojo.PurchaseOrderModify;
import com.example.server.mapper.PurchaseOrderModifyMapper;
import com.example.server.service.IPurchaseOrderModifyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-29
 */
@Service
public class PurchaseOrderModifyServiceImpl extends ServiceImpl<PurchaseOrderModifyMapper, PurchaseOrderModify> implements IPurchaseOrderModifyService {

	@Autowired
	private PurchaseOrderModifyMapper purchaseOrderModifyMapper;

	/**
	 * 判断当前用户能修改采购单中哪些字段
	 */
	@Override
	public PurchaseOrderModify isModifyFiled(String roleIds) {
		return purchaseOrderModifyMapper.isModifyFiled(roleIds);
	}
}
