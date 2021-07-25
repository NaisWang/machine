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
 * @since 2021-07-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_up_shelf_receipt")
@ApiModel(value = "UpShelfReceipt对象", description = "")
public class UpShelfReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "上架单号")
	@TableId(value = "up_shelf_order", type = IdType.AUTO)
	private Integer upShelfOrder;

	@ApiModelProperty(value = "上架日期")
	@TableField("up_shelf_date")
	private LocalDate upShelfDate;

	@ApiModelProperty(value = "机器数量")
	@TableField(exist = false)
	private Integer sum;

	@ApiModelProperty(value = "备注")
	private String comment;

	@ApiModelProperty(value = "操作人id")
	@TableField("operate_emp_id")
	private Integer operateEmpId;


}
