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
 * @since 2021-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_purchase_return_receipt")
@ApiModel(value = "PurchaseReturnReceipt对象", description = "")
public class PurchaseReturnReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "采购退货单号")
	@TableId(value = "purchase_return_order", type = IdType.AUTO)
	private Integer purchaseReturnOrder;

	@ApiModelProperty(value = "采购退货日期")
	@TableField("purchase_return_date")
	private LocalDate purchaseReturnDate;

	@ApiModelProperty(value = "退货总数")
	@TableField(exist = false)
	private Integer sum;

	@ApiModelProperty(value = "退货已寄出")
	@TableField(exist = false)
	private Integer returnAndSend;

	@ApiModelProperty(value = "退货已收款")
	@TableField(exist = false)
	private Integer returnAndProceeds;

	@ApiModelProperty(value = "退货未寄出")
	@TableField(exist = false)
	private Integer returnAndNotSend;

	@ApiModelProperty(value = "是否发布")
	@TableField("is_release")
	private Integer isRelease;

	@ApiModelProperty(value = "备注")
	private String comment;


}
