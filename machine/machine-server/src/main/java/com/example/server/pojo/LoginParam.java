package com.example.server.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : whz
 */
@Data
public class LoginParam {

	@ApiModelProperty(value = "用户名", required = true)
	private String username;
	@ApiModelProperty(value = "密码", required = true)
	private String password;

}
