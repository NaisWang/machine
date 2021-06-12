package com.example.server.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@TableName("t_purchase_order")
@ApiModel(value = "PurchaseOrder对象", description = "")
public class PurchaseOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "物品编号")
	@Excel(name = "物品编号")
	private String number;

	@ApiModelProperty(value = "IMEI号")
	@Excel(name = "IMEI号")
	private String imei;

	@ApiModelProperty(value = "品类id")
	@TableField("category_id")
	private Integer categoryId;

	@ApiModelProperty(value = "品类")
	@ExcelEntity(name = "品类")
	@TableField(exist = false)
	private Category category;

	@ApiModelProperty(value = "型号")
	@Excel(name = "型号")
	private String type;

	@ApiModelProperty(value = "品牌id")
	private Integer brandId;

	@ApiModelProperty(value = "品牌")
	@ExcelEntity(name = "品牌")
	@TableField(exist = false)
	private Brand brand;

	@ApiModelProperty(value = "sku名称")
	@Excel(name = "sku名称")
	private String sku;

	@ApiModelProperty(value = "等级")
	@Excel(name = "等级")
	@TableField("`rank`")
	private String rank;

	@ApiModelProperty(value = "购买渠道")
	@ExcelEntity(name = "购买渠道")
	@TableField(exist = false)
	private Channel channel;

	@ApiModelProperty(value = "渠道最初值")
	@TableField(exist = false)
	private Integer channelOriginValue;

	@ApiModelProperty(value = "机器状态id")
	@TableField("status_id")
	private Integer statusId;

	@ApiModelProperty(value = "机器状态")
	@TableField(exist = false)
	private MachineStatus machineStatus;

	@ApiModelProperty(value = "库位")
	@Excel(name = "库位")
	@TableField("stock_location")
	private String stockLocation;

	@ApiModelProperty(value = "购买渠道id")
	@TableField("purchasing_channel_id")
	private Integer purchasingChannelId;

	@ApiModelProperty(value = "录入批次")
	@TableField("enter_batch")
	@Excel(name = "录入批次")
	private Integer enterBatch;

	@ApiModelProperty(value = "采购价")
	@TableField("purchase_price")
	@Excel(name = "采购价")
	private Float purchasePrice;

	@ApiModelProperty(value = "中标日期")
	@TableField("bidding_date")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
	@Excel(name = "中标日期", format = "yyyy-MM-dd")
	private LocalDate biddingDate;

	@ApiModelProperty(value = "操作了这个机器的所有操作人id")
	@TableField("operated_ids")
	private String operatedIds;

	@ApiModelProperty(value = "正在操作这个机器的操作人id")
	@TableField("operating_id")
	private Integer operatingId;

	@ApiModelProperty(value = "备注")
	@TableField("`describe`")
	private String describe;

}
