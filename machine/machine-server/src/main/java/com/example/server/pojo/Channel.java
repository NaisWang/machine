package com.example.server.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_channel")
@ApiModel(value = "Channel对象", description = "")
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "采购渠道、出库渠道名称")
	@Excel(name = "购买渠道")
	private String name;

	@ApiModelProperty(value = "金钱")
	private Float money;

	@ApiModelProperty(value = "采购金额")
	@TableField(exist = false)
	private Float purchaseSumPrice;

	@ApiModelProperty(value = "销售金额")
	@TableField(exist = false)
	private Float marketSellPrice;

	@ApiModelProperty(value = "销售退货")
	@TableField(exist = false)
	private Float marketRefundPrice;

	@ApiModelProperty(value = "备注")
	private String comment;

}
