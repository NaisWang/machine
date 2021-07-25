package com.example.server.pojo;

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
 * @since 2021-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_paiji_backup")
@ApiModel(value = "PaijiBackup对象", description = "")
public class PaijiBackup implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "描述")
	private String value;

	@ApiModelProperty(value = "父描述")
	private String parentValue;

	@ApiModelProperty(value = "字段")
	private String name;

	@ApiModelProperty(value = "小当检验的描述字段")
	@TableField("xd_checkout")
	private String xdCheckout;

	@ApiModelProperty(value = "与上一个版本的区别")
	private Integer status;

	@ApiModelProperty(value = "爱回收中检验的描述字段")
	@TableField("ahs_checkout")
	private String ahsCheckout;


}
