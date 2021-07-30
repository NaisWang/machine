package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.tomcat.jni.Local;

/**
 * <p>
 *
 * </p>
 *
 * @author whz
 * @since 2021-07-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_machine_trace")
@ApiModel(value = "MachineTrace对象", description = "")
public class MachineTrace implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = " id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "物品编码")
	private String number;

	@ApiModelProperty(value = "imei号")
	private String imei;

	@ApiModelProperty(value = "拍机堂条码")
	@TableField("paiji_barcode")
	private String paijiBarcode;

	@ApiModelProperty(value = "机器状态")
	@TableField("status_id")
	private Integer statusId;

	@ApiModelProperty(value = "转交状态id")
	private Integer deliverStatusId;

	@ApiModelProperty(value = "转交类型id")
	private Integer deliverIntentionId;

	@ApiModelProperty(value = "单据id")
	@TableField("receipt_id")
	private Integer receiptId;

	@ApiModelProperty(value = "时间")
	private LocalDateTime time;

	@ApiModelProperty(value = "操作人")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "备注")
	private String comment;

	@ApiModelProperty(value = "库位")
	@TableField("storage_location_id")
	private Integer storageLocationId;

	@ApiModelProperty(value = "是否可以上架")
	@TableField("is_up_shelf")
	private Integer isUpShelf;

	@ApiModelProperty(value = "是否为召回")
	@TableField("is_recall")
	private Integer isRecall;


	public MachineTrace(String number, Integer statusId, Integer receiptId, LocalDateTime time, Integer operateEmpId, String comment, Integer storageLocationId) {
		this.number = number;
		this.statusId = statusId;
		this.receiptId = receiptId;
		this.time = time;
		this.operateEmpId = operateEmpId;
		this.comment = comment;
		this.storageLocationId = storageLocationId;
	}

}
