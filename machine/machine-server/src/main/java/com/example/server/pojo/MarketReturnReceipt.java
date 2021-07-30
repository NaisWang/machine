package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDateTime;

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
 * @since 2021-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_market_return_receipt")
@ApiModel(value = "MarketReturnReceipt对象", description = "")
public class MarketReturnReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "销售退货单号")
	@TableId(value = "market_return_order", type = IdType.AUTO)
	private Integer marketReturnOrder;

	@ApiModelProperty(value = "创建时间")
	@TableField("create_time")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "发布时间")
	@TableField("release_time")
	private LocalDateTime releaseTime;

	@ApiModelProperty(value = "操作人")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "退款金额")
	@TableField(exist = false)
	private Float refundAmount;

	@ApiModelProperty(value = "是否发布")
	@TableField("is_release")
	private Integer isRelease;

	@ApiModelProperty(value = "备注")
	private String comment;


}
