package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

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
 * @since 2021-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_send_fix_machine")
@ApiModel(value = "SendFixMachine对象", description = "")
public class SendFixMachine implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "send_fix_machine_id", type = IdType.AUTO)
	private Integer sendFixMachineId;

	@ApiModelProperty(value = "送修时间")
	@TableField("send_fix_date")
	private LocalDateTime sendFixDate;

	@ApiModelProperty(value = "取回时间")
	@TableField("receive_time")
	private LocalDateTime receiveTime;

	@ApiModelProperty(value = "表单id")
	@TableField("receipt_id")
	private Integer receiptId;

	@ApiModelProperty(value = "是否为返修")
	@TableField("is_repair")
	private Integer isRepair;

	@ApiModelProperty(value = "机器物品编码")
	@TableField("number")
	private String number;

	@ApiModelProperty(value = "维修项")
	@TableField("fix_item")
	private String fixItem;

	@ApiModelProperty(value = "维修当口")
	@TableField("send_destination")
	private String sendDestination;

	@ApiModelProperty(value = "机器状态")
	@TableField("fix_status")
	private Integer fixStatus;


	@ApiModelProperty(value = "备注")
	private String fixComment;


}
