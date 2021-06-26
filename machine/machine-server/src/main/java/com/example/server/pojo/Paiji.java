package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

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
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_paiji")
@ApiModel(value = "Paiji对象", description = "")
public class Paiji implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "描述")
	private String value;

	@ApiModelProperty(value = "字段")
	private String name;

	@ApiModelProperty(value = "小当检验的描述字段")
	private String xdCheckout;

	@ApiModelProperty(value = "爱回收检验的描述字段")
	private String ahsCheckout;

	@ApiModelProperty(value = "与上一个版本的区别")
	private Integer status;

}
