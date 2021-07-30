package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
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
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_operate_trace")
@ApiModel(value = "OperateTrace对象", description = "")
public class OperateTrace implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "机器物品编码")
	@NonNull
	private String number;

	@ApiModelProperty(value = "操作类型，1：成色检测，2：功能检测，3：上架，4：确定维修项，5：维修完成")
	@TableField("operate_type")
	@NonNull
	private Integer operateType;

	@ApiModelProperty(value = "操作前状态")
	@TableField("before_status_id")
	@NonNull
	private Integer beforeStatusId;

	@ApiModelProperty(value = "操作后状态")
	@TableField("after_status_id")
	@NonNull
	private Integer afterStatusId;

	@ApiModelProperty(value = "操作时间")
	@TableField("operate_time")
	@NonNull
	private LocalDateTime operateTime;

	@ApiModelProperty(value = "操作人")
	@TableField("operate_emp_id")
	@NonNull
	private Integer operateEmpId;

	@ApiModelProperty(value = "库位id")
	@TableField("storage_location_id")
	@NonNull
	private Integer storageLocationId;


}
