package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.SendFixReceipt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
public interface SendFixReceiptMapper extends BaseMapper<SendFixReceipt> {

	/**
	 * 创建外修订单
	 */
	IPage<SendFixReceipt> getSendFixReceipt(Page<SendFixReceipt> page, SendFixReceipt sendFixReceipt);
}
