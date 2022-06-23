package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-09-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_up_shelf_enter_storage_receipt_to_machine")
@ApiModel(value = "UpShelfEnterStorageReceiptToMachine对象", description = "")
public class UpShelfEnterStorageReceiptToMachine implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "单据id")
	@TableField("receipt_id")
	private Integer receiptId;

	@ApiModelProperty(value = "机器id")
	@TableField("machine_id")
	private Integer machineId;

	@ApiModelProperty(value = "机器编码")
	@TableField("machine_number")
	private String machineNumber;

	@ApiModelProperty(value = "sku")
	@TableField("machine_sku")
	private String machineSku;


}
