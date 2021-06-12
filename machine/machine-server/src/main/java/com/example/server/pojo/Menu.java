package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

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
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_menu")
@ApiModel(value = "Menu对象", description = "")
public class Menu implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "path")
	private String path;

	@ApiModelProperty(value = "组件文件名")
	private String component;

	@ApiModelProperty(value = "组件名称")
	private String name;

	@ApiModelProperty(value = "菜单图标")
	private String iconcls;

	@ApiModelProperty(value = "父组件id")
	@TableField("parent_id")
	private Integer parentId;

	@ApiModelProperty(value = "能访问该菜单的角色id")
	@TableField("role_ids")
	private String roleIds;

	@ApiModelProperty(value = "子组件")
	@TableField(exist = false)
	private List<Menu> children;

}
