package com.example.server.mapper;

import com.example.server.pojo.PurchaseOrderModify;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-05-29
 */
public interface PurchaseOrderModifyMapper extends BaseMapper<PurchaseOrderModify> {

	/**
	 * 判断当前用户能修改采购单中哪些字段
	 */
	PurchaseOrderModify isModifyFiled(@Param("roleIds") String roleIds);
}
