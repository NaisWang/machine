package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
@TableName("t_up_shelf_enter_storage_receipt")
@ApiModel(value = "UpShelfEnterStorageReceipt对象", description = "")
public class UpShelfEnterStorageReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = " 创建日期")
	@TableField("create_time")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "发布日期")
	@TableField("release_time")
	private LocalDateTime releaseTime;

	@ApiModelProperty(value = "备注")
	private String comment;

	@ApiModelProperty(value = "数量")
	@TableField(exist = false)
	private Integer sum;

	@ApiModelProperty(value = "是否发布")
	@TableField("is_release")
	private Integer isRelease;

	@ApiModelProperty(value = "操作人id")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "库位")
	private Integer storageLocationId;

}
