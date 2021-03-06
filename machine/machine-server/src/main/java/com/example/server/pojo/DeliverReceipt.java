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
 * @since 2021-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_deliver_receipt")
@ApiModel(value = "DeliverReceipt对象", description = "")
public class DeliverReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "deliver_receipt_id", type = IdType.AUTO)
	private Integer deliverReceiptId;

	@ApiModelProperty(value = "创建时间")
	@TableField("create_time")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "发布时间")
	@TableField("release_time")
	private LocalDateTime releaseTime;

	@ApiModelProperty(value = "转交意图id")
	@TableField("deliver_intention_id")
	private Integer deliverIntentionId;

	@ApiModelProperty(value = "接收人id")
	@TableField("receive_emp_ids")
	private String receiveEmpIds;

	@ApiModelProperty(value = "机器总数")
	@TableField(exist = false)
	private Integer sum;

	@ApiModelProperty(value = "未接收总数")
	@TableField(exist = false)
	private Integer notReceiveSum;

	@ApiModelProperty(value = "完成指标总数")
	@TableField(exist = false)
	private Integer completeSum;

	@ApiModelProperty(value = "操作人id")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "是否可以修改，0：可以修改，1：不可修改")
	@TableField("enable_edit")
	private Integer enableEdit;

	@ApiModelProperty(value = "是否可以删除")
	@TableField(exist = false)
	private Integer isDelete;

	@ApiModelProperty(value = "如果是库位调拨单据，则需要选择一个库位")
	@TableField("storage_location_id")
	private Integer storageLocationId;

	@ApiModelProperty(value = "如果是库位调拨单据，则其对应的入库单据id")
	@TableField("enter_storage_receipt_id")
	private Integer enterStorageReceiptId;

	@ApiModelProperty(value = "备注")
	private String comment;

}
