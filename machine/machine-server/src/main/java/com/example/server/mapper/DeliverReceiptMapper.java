package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.DeliverReceipt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-22
 */
public interface DeliverReceiptMapper extends BaseMapper<DeliverReceipt> {

	/**
	 * 获取转交表数据
	 */
	IPage<DeliverReceipt> getDeliverReceipt(Page<DeliverReceipt> page, @Param("deliverReceipt") DeliverReceipt deliverReceipt, @Param("localDateScope") LocalDate[] localDateScope);

	/**
	 * 获取需要接收的转交表
	 */
	IPage<DeliverReceipt> getReceiveDeliverReceipt(Page<DeliverReceipt> deliverReceiptPage, @Param("deliverReceipt") DeliverReceipt deliverReceipt, @Param("empId") Integer empId);
}
