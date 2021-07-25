package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2021-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_machine_desc")
@ApiModel(value = "MachineDesc对象", description = "")
public class MachineDesc implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "描述")
	private String value;

	@ApiModelProperty(value = "字段")
	private String name;

	@ApiModelProperty(value = "phone是否适用")
	private Integer phone;

	@ApiModelProperty(value = "平板是否适用")
	private Integer tablet;

	@ApiModelProperty(value = "手表是否适用")
	private Integer watch;

	@ApiModelProperty(value = "父描述")
	@TableField("parent_value")
	private String parentValue;

}
