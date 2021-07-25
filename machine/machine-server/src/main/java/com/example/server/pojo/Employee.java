package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.server.config.security.CustomAuthorityDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@TableName("t_employee")
@ApiModel(value = "Employee对象", description = "")
public class Employee implements Serializable, UserDetails {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "姓名")
	private String name;

	@ApiModelProperty(value = "用户名")
	private String username;

	@ApiModelProperty(value = "邮箱")
	private String email;

	@ApiModelProperty(value = "密码")
	private String password;

	@ApiModelProperty(value = "员工头像id")
	private Integer image;

	@ApiModelProperty(value = "员工库位")
	private Integer storageLocationId;

	@ApiModelProperty(value = "员工角色")
	@TableField(exist = false)
	private List<Role> roles;

	@Override
	@JsonDeserialize(using = CustomAuthorityDeserializer.class)
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (roles != null) {
			List<SimpleGrantedAuthority> authorityList = roles.stream().map(role -> new SimpleGrantedAuthority(role.getEnName())).collect(Collectors.toList());
			return authorityList;
		}
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
