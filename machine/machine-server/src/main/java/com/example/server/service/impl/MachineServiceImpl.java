package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.controller.MachineController;
import com.example.server.mapper.MachineMapper;
import com.example.server.pojo.EnterStorageReceipt;
import com.example.server.pojo.Machine;
import com.example.server.pojo.PurchaseOrderReceipt;
import com.example.server.service.IMachineService;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-07-07
 */
@Service
public class MachineServiceImpl extends ServiceImpl<MachineMapper, Machine> implements IMachineService {

	@Autowired
	private MachineMapper machineMapper;

	/**
	 * 获取采购单信息
	 */
	public RespPageBean getPurchaseOrder(Integer currentPage, Integer size, PurchaseOrderReceipt purchaseOrder) {
		Page<PurchaseOrderReceipt> purchaseOrderPage = new Page<>(currentPage, size);
		IPage<PurchaseOrderReceipt> iPage = machineMapper.getPurchaseOrder(purchaseOrderPage, purchaseOrder);
		RespPageBean respPageBean = new RespPageBean(iPage.getTotal(), iPage.getRecords());
		return respPageBean;
	}

	public RespPageBean getMachine(Integer currentPage, Integer size, Machine machine, LocalDate[] bidDateScope) {
		Page<Machine> page = new Page<>(currentPage, size);
		IPage<Machine> iPage = machineMapper.getMachine(page, machine, bidDateScope);
		RespPageBean respPageBean = new RespPageBean(iPage.getTotal(), iPage.getRecords());
		return respPageBean;
	}

	/**
	 * 一键入库(更新机器入库信息)
	 */
	@Override
	public Boolean updateEnterStorageReceiptForOneKey(EnterStorageReceipt enterStorageReceipt, Integer purchaseOrderId, LocalDateTime nowDatetime, Integer empId) {
		return machineMapper.updateEnterStorageReceiptForOneKey(enterStorageReceipt, purchaseOrderId, nowDatetime, empId);
	}

	/**
	 * 修改机器转交状态
	 */
	public Boolean modifyMachineDeliverStatus(Integer[] ids, Integer deliverReceiptId) {
		return machineMapper.update(new Machine(), new UpdateWrapper<Machine>().in("id", ids).set("deliver_machine_id", deliverReceiptId)) != 0;
	}

}
