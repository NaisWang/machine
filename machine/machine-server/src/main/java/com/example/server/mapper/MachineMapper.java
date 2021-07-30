package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.*;
import net.bytebuddy.asm.Advice;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.yaml.snakeyaml.error.Mark;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-07
 */
public interface MachineMapper extends BaseMapper<Machine> {


	/**
	 * 获取采购单信息
	 */
	IPage<PurchaseOrderReceipt> getPurchaseOrder(Page<PurchaseOrderReceipt> page, @Param("purchase") PurchaseOrderReceipt purchaseOrderReceipt);

	/**
	 * 获取入库单信息
	 */
	IPage<EnterStorageReceipt> getEnterStorageReceipt(Page<EnterStorageReceipt> page, @Param("enterStorageReceipt") EnterStorageReceipt enterStorageReceipt);

	/**
	 * 获取销退入库单信息
	 */
	IPage<MarketReturnEnterStorage> getMarketReturnEnterStorageReceipt(Page<MarketReturnEnterStorage> page, @Param("marketReturnEnterStorage") MarketReturnEnterStorage marketReturnEnterStorage);

	/**
	 * 获取采购退货信息
	 */
	IPage<PurchaseReturnReceipt> getPurchaseReturnReceipt(Page<PurchaseReturnReceipt> purchaseReturnReceiptPage, @Param("purchaseReturnReceipt") PurchaseReturnReceipt purchaseReturnReceipt);

	/**
	 * 获取销售订单信息
	 */
	IPage<MarketOrderReceipt> getMarketOrderReceipt(Page<MarketOrderReceipt> page, @Param("marketOrderReceipt") MarketOrderReceipt marketOrderReceipt);

	/**
	 * 获取销售退货订单
	 */
	IPage<MarketReturnReceipt> getMarketReturnReceipt(Page<MarketReturnReceipt> marketReturnReceiptPage, @Param("marketReturnReceipt") MarketReturnReceipt marketReturnReceipt);

	/**
	 * 获取机器信息
	 */
	IPage<Machine> getMachine(Page<Machine> page, @Param("machine") Machine machine, @Param("bidDateScope") LocalDate[] bidDateScope);

	/**
	 * 获取上架单据
	 */
	IPage<UpShelfReceipt> getUpShelfReceipt(Page<UpShelfReceipt> page, @Param("localDateScope") LocalDate[] localDateScope, @Param("upShelfReceipt") UpShelfReceipt upShelfReceipt);

	/**
	 * 一键入库(更新机器入库信息)
	 */
	Boolean updateEnterStorageReceiptForOneKey(@Param("enterStorageReceipt") EnterStorageReceipt enterStorageReceipt, @Param("purchaseOrderId") Integer purchaseOrderId, @Param("nowDateTime") LocalDateTime nowDateTime, @Param("empId") Integer empId);

	/**
	 * 获取上架入库单
	 */
	IPage<UpShelfEnterStorageReceipt> getUpShelfEnterStorageReceipt(Page<UpShelfEnterStorageReceipt> page, @Param("upShelfEnterStorageReceipt") UpShelfEnterStorageReceipt upShelfEnterStorageReceipt);
}
