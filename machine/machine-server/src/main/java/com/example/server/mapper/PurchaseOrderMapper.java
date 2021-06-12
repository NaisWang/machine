package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.PurchaseOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.utils.RespBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

	/**
	 * 搜索机器
	 */
	List<PurchaseOrder> searchMachine();


	/**
	 * 分页获取采购单中的机器数据
	 */
	IPage<PurchaseOrder> getMachineByPage(Page<PurchaseOrder> page, @Param("purchaseOrder") PurchaseOrder purchaseOrder, @Param("bidDateScope") LocalDate[] bidDateScope);
}
