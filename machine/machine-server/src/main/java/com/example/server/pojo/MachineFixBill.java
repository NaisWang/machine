package com.example.server.pojo;

import java.math.BigDecimal;

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
@TableName("t_machine_fix_bill")
@ApiModel(value = "MachineFixBill对象", description = "")
public class MachineFixBill implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "机器id")
	@TableField("machine_id")
	private Integer machineId;

	@ApiModelProperty(value = "机器物品编码")
	@TableField("machine_number")
	private String machineNumber;

	@ApiModelProperty(value = "sku")
	@TableField("machine_sku")
	private String machineSku;

	@ApiModelProperty(value = "维修价格")
	@TableField("fix_price")
	private Float fixPrice;

	@ApiModelProperty(value = "赔款")
	private Float reparation;

	@ApiModelProperty(value = "时间")
	private LocalDateTime time;

	@ApiModelProperty(value = "操作人")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "备注")
	@TableField("comment")
	private String comment;

	@ApiModelProperty(value = "是否确认")
	@TableField("confirm")
	private Integer confirm;

}
