package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;

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
 * @since 2021-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_enter_storage_receipt")
@ApiModel(value = "EnterStorageReceipt对象", description = "")
public class EnterStorageReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "入库单据编号")
	@TableId(value = "enter_storage_order", type = IdType.AUTO)
	private Integer enterStorageOrder;

	@ApiModelProperty(value = "入库日期")
	@TableField("enter_storage_date")
	private LocalDate enterStorageDate;

	@ApiModelProperty(value = "数量")
	@TableField(exist = false)
	private Integer sum;

	@ApiModelProperty(value = "是否发布")
	@TableField("is_release")
	private Integer isRelease;

	@ApiModelProperty(value = "备注")
	@TableField("`comment`")
	private String comment;

	@ApiModelProperty(value = "总价格")
	@TableField(exist = false)
	private Float sumPrice;


}
