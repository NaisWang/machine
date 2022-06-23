package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

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
 * @since 2021-08-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_market_order_receipt_to_machine")
@ApiModel(value = "MarketOrderReceiptToMachine对象", description = "")
public class MarketOrderReceiptToMachine implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "单据id")
	@TableField("receipt_id")
	private Integer receiptId;

	@ApiModelProperty(value = "机器id")
	@TableField("machine_id")
	private Integer machineId;

	@ApiModelProperty(value = "物品编码")
	@TableField("machine_number")
	private String machineNumber;

	@ApiModelProperty(value = "sku")
	@TableField("machine_sku")
	private String machineSku;

	@ApiModelProperty(value = "备注")
	private String comment;

	@ApiModelProperty(value = "操作人")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "操作时间")
	@TableField("operate_time")
	private LocalDateTime operateTime;

	@ApiModelProperty(value = "销售价格")
	@TableField("sell_price")
	private Float sellPrice;

}
