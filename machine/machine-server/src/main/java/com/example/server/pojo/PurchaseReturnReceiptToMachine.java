package com.example.server.pojo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author whz
 * @since 2021-09-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_purchase_return_receipt_to_machine")
@ApiModel(value = "PurchaseReturnReceiptToMachine对象", description = "")
public class PurchaseReturnReceiptToMachine implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "单据id")
	@TableField("receipt_id")
	private Integer receiptId;

	@ApiModelProperty(value = "物品编码")
	@TableField("machine_number")
	private String machineNumber;

	@ApiModelProperty(value = "机器id")
	@TableField("machine_id")
	private Integer machineId;

	@ApiModelProperty(value = "机器sku")
	@TableField("machine_sku")
	private String machineSku;

	@ApiModelProperty(value = "采购价")
	@TableField("purchase_price")
	private Float purchasePrice;

	@ApiModelProperty(value = "状态Id")
	@TableField("status_id")
	private Integer statusId;

}
