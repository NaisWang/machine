package com.example.server.config.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author : whz
 */
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
	@Override
	public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
		for (ConfigAttribute configAttribute : collection) {
			String needRole = configAttribute.getAttribute();
			if ("ROLE_LOGIN".equals(needRole)) {
				if (authentication instanceof AnonymousAuthenticationToken) {
					throw new AccessDeniedException("尚未登陆，请登陆");
				} else {
					throw new AccessDeniedException("权限不足，请联系管理员");
				}
			}
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			System.out.println("当前用户的角色" + authorities.toString());
			for (GrantedAuthority grantedAuthority : authorities) {
				if (grantedAuthority.getAuthority().equals(needRole)) {
					return;
				}
			}
		}
		throw new AccessDeniedException("权限不足，请联系管理员");
	}

	@Override
	public boolean supports(ConfigAttribute configAttribute) {
		return false;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return false;
	}
}
