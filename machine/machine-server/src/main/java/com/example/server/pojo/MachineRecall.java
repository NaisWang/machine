package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDateTime;

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
 * @since 2021-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_machine_recall")
@ApiModel(value = "MachineRecall对象", description = "")
public class MachineRecall implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "机器number")
	private String number;

	@ApiModelProperty(value = "从何处召回")
	@TableField("pre_operate_emp_id")
	private Integer preOperateEmpId;

	@ApiModelProperty(value = "操作人")
	@TableField("now_operate_emp_id")
	private Integer nowOperateEmpId;

	@ApiModelProperty(value = "召回时机器状态id")
	@TableField("status_id")
	private Integer statusId;

	@ApiModelProperty(value = "库位id")
	@TableField("storage_location_id")
	private Integer storageLocationId;

	@ApiModelProperty(value = "是否可以上架")
	@TableField(exist = false)
	private Integer isUpShelf;

	@ApiModelProperty(value = "召回日期")
	@TableField("recall_time")
	private LocalDateTime recallTime;

}
