package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

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
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_url")
@ApiModel(value = "Url对象", description = "")
public class Url implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "请求url")
	private String url;

	@ApiModelProperty(value = "请求方式")
	private Integer method;

	@ApiModelProperty(value = "url描述")
	private String descr;

	@ApiModelProperty(value = "角色id")
	private String roleIds;

	@ApiModelProperty(value = "角色英文名")
	@TableField(exist = false)
	private List<String> roleName;

}
