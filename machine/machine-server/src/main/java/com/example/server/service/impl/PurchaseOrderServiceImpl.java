package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.PurchaseOrder;
import com.example.server.mapper.PurchaseOrderMapper;
import com.example.server.service.IPurchaseOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements IPurchaseOrderService {

	@Autowired
	private PurchaseOrderMapper purchaseOrderMapper;

	/**
	 * 手动向采购单中添加机器
	 */
	@Override
	public Integer manualAddMachine(PurchaseOrder purchaseOrder) {
		return purchaseOrderMapper.insert(purchaseOrder);
	}

	/**
	 * 分页获取采购单中的机器数据
	 */
	@Override
	public RespPageBean getMachineByPage(Integer currentPage, Integer size, PurchaseOrder purchaseOrder, LocalDate[] bidDateScope) {
		Page<PurchaseOrder> page = new Page<>(currentPage, size);
		IPage<PurchaseOrder> purchaseOrderIPage = purchaseOrderMapper.getMachineByPage(page, purchaseOrder, bidDateScope);
		RespPageBean respPageBean = new RespPageBean(purchaseOrderIPage.getTotal(), purchaseOrderIPage.getRecords());
		return respPageBean;
	}

}
