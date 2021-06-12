package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.service.impl.RoleServiceImpl;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

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
@TableName("t_role_url_method")
@ApiModel(value = "RoleUrl对象", description = "")
public class RoleUrlMethod implements Serializable {

	@Autowired
	private RoleServiceImpl roleService;

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "角色id")
	@TableField("role_id")
	private Integer roleId;

	@ApiModelProperty(value = "角色英文名")
	@TableField(exist = false)
	private String roleEnName;

	@ApiModelProperty(value = "请求url的id")
	@TableField("url_id")
	private Integer urlId;

	@ApiModelProperty(value = "请求url")
	@TableField(exist = false)
	private String url;

	@ApiModelProperty(value = "请求方法的ids")
	@TableField("method_ids")
	private String methodIds;

}
