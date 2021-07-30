package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

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
@TableName("t_send_fix_receipt")
@ApiModel(value = "SendFixReceipt对象", description = "")
public class SendFixReceipt implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "创建时间")
	@TableField("create_time")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "发布时间")
	@TableField("release_time")
	private LocalDateTime releaseTime;

	@ApiModelProperty(value = "操作人id")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "是否发布")
	@TableField("is_release")
	private Integer isRelease;

	@ApiModelProperty(value = "备注")
	private String comment;

	@ApiModelProperty(value = "机器总数")
	@TableField(exist = false)
	private Integer sum;

	@ApiModelProperty(value = "已取回机器总数")
	@TableField(exist = false)
	private Integer receiveSum;

}
