package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author whz
 * @since 2021-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_purchase_order_receipt")
@ApiModel(value = "PurchaseOrderReceipt对象", description = "")
public class PurchaseOrderReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "采购单号")
	@TableId(value = "purchase_order", type = IdType.AUTO)
	private Integer purchaseOrder;

	@ApiModelProperty(value = "采购日期")
	@TableField("purchase_date")
	private LocalDate purchaseDate;

	@ApiModelProperty(value = "采购渠道id")
	@TableField("purchase_channel_id")
	private Integer purchaseChannelId;

	@ApiModelProperty(value = "操作人id")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "备注")
	private String comment;

	@ApiModelProperty(value = "采购数量")
	@TableField(exist = false)
	private Integer sum;

	@ApiModelProperty(value = "采购金额")
	@TableField(exist = false)
	private Float sumPrice;

	@ApiModelProperty(value = "未入库机器总额")
	@TableField(exist = false)
	private Float notEnterPurchasePrice;

	@ApiModelProperty(value = "已入库数量")
	@TableField(exist = false)
	private Integer enterPurchaseCount;

	@ApiModelProperty(value = "未入库数量")
	@TableField(exist = false)
	private Integer notEnterPurchaseCount;

	//采购单状态;
	// 0: 待入库； 1：已入库； 2：部分入库
	@ApiModelProperty(value = "采购单状态")
	@TableField(exist = false)
	private Integer purchaseOrderStatus;

	@ApiModelProperty(value = "是否提交")
	@TableField("is_release")
	private Integer isRelease;

}
