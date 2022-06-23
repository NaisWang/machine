package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author whz
 * @since 2021-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_machine_detection")
@ApiModel(value = "MachineDetection对象", description = "")
public class MachineDetection implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "机器id")
	@TableField("machine_id")
	private Integer machineId;

	@ApiModelProperty(value = "机器状态id")
	@TableField("machine_status_id")
	private Integer machineStatusId;

	@ApiModelProperty(value = "物品编码")
	@TableField("machine_number")
	private String machineNumber;

	@ApiModelProperty(value = "成色描述")
	@TableField("quality_desc")
	private String qualityDesc;

	@ApiModelProperty(value = "功能描述")
	@TableField("feature_desc")
	private String featureDesc;

	@ApiModelProperty(value = "确定维修项")
	@TableField("need_fix_items")
	private String needFixItems;

	@ApiModelProperty(value = "操作人id")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "操作人时间")
	@TableField("operate_time")
	private LocalDateTime operateTime;

	@ApiModelProperty(value = "备注")
	@TableField("comment")
	private String comment;

	@ApiModelProperty(value = "成色检测备注")
	@TableField("quality_comment")
	private String qualityComment;

	@ApiModelProperty(value = "功能检测备注")
	@TableField("feature_comment")
	private String featureComment;

	@ApiModelProperty(value = "确定维修项备注")
	@TableField("need_fix_comment")
	private String needFixComment;
}
