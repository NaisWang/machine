package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2021-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_deliver_machine")
@ApiModel(value = "DeliverMachine对象", description = "")
public class DeliverMachine implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = " id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "转交单id")
	@TableField("deliver_receipt_id")
	private Integer deliverReceiptId;

	@ApiModelProperty(value = "机器id")
	@TableField("machine_id")
	private Integer machineId;

	@ApiModelProperty(value = "机器物品编码")
	@TableField("machine_number")
	private String machineNumber;

	@ApiModelProperty(value = "接收人id")
	@TableField("receive_emp_id")
	private Integer receiveEmpId;

	@ApiModelProperty(value = "接收时间")
	@TableField("receive_date")
	private LocalDateTime receiveDate;

	@ApiModelProperty(value = "转交状态，0：转交中；1：已接收；2：拒收")
	private Integer status;

	@ApiModelProperty(value = "是否已完成")
	private Integer isComplete;

	@ApiModelProperty(value = "备注")
	private String comment;


}
