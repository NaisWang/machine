package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
 * @since 2021-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_machine_table_field")
@ApiModel(value = "TableField对象", description = "")
public class MachineTableField implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = " id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "表单类型")
	private String type;

	@ApiModelProperty(value = "中文名")
	@TableField("name_zh")
	private String nameZh;

	@ApiModelProperty(value = "英文名")
	@TableField("name_en")
	private String nameEn;

	@ApiModelProperty(value = "是否可修改， 0：不可修改，1：可修改")
	private Integer edit;

	@ApiModelProperty(value = "当为添加时是否显示，0：显示，1：不显示")
	private Integer showWhenAdd;

}
