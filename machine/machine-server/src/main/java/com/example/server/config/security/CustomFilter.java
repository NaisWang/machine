package com.example.server.config.security;

import com.example.server.pojo.RoleUrlMethod;
import com.example.server.service.impl.RequestMethodServiceImpl;
import com.example.server.service.impl.RoleUrlMethodServiceImpl;
import com.example.server.service.impl.UrlServiceImpl;
import com.example.server.utils.RequestMethodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : whz
 */
@Component
public class CustomFilter implements FilterInvocationSecurityMetadataSource {
	@Autowired
	private UrlServiceImpl urlService;
	@Autowired
	private RoleUrlMethodServiceImpl roleUrlMethodService;
	@Autowired
	private RequestMethodServiceImpl requestMethodService;


	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

		String requestUrl = ((FilterInvocation) o).getRequestUrl();
		String method = ((FilterInvocation) o).getHttpRequest().getMethod();
		List<RoleUrlMethod> roleUrlMethods = roleUrlMethodService.getRoleUrlMethodList();
		List<String> allowRoles = new LinkedList<>();


		for (RoleUrlMethod roleUrlMethod : roleUrlMethods) {
			if (antPathMatcher.match(roleUrlMethod.getUrl(), requestUrl) && RequestMethodUtil.isIncludeMethod(method, roleUrlMethod.getMethodIds())) {
				allowRoles.add(roleUrlMethod.getRoleEnName());
			}
		}
		if (!allowRoles.isEmpty()) {
			System.out.println("允许访问该请求的角色" + allowRoles.toString());
			return SecurityConfig.createList(allowRoles.toArray(String[]::new));
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
