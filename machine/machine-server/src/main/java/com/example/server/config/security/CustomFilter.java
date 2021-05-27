package com.example.server.config.security;

import com.example.server.pojo.Url;
import com.example.server.service.impl.RoleServiceImpl;
import com.example.server.service.impl.UrlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * @author : whz
 */
@Component
public class CustomFilter implements FilterInvocationSecurityMetadataSource {
	@Autowired
	private UrlServiceImpl urlService;

	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
		String requestUrl = ((FilterInvocation) o).getRequestUrl();
		List<Url> urls = urlService.getUrlsWithRole();
		for (Url url : urls) {
			if (antPathMatcher.match(url.getUrl(), requestUrl)) {
				String[] str = url.getRoleName().toArray(String[]::new);
				System.out.println(str);
				return SecurityConfig.createList(str);
			}
		}
		return SecurityConfig.createList("ROLE_LOGIN");
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return false;
	}
}
