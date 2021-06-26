package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
 * @since 2021-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_paiji_user")
@ApiModel(value = "PaijiUser对象", description = "")
public class PaijiUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "电话号码")
	private String username;

	@ApiModelProperty(value = "密码")
	private String password;


}
