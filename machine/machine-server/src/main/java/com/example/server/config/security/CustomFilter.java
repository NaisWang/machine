package com.example.server.config.security;

import com.example.server.pojo.Role;
import com.example.server.service.impl.RequestMethodServiceImpl;
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
import java.util.stream.Collectors;

/**
 * @author : whz
 */
@Component
public class CustomFilter implements FilterInvocationSecurityMetadataSource {
	@Autowired
	private UrlServiceImpl urlService;
	@Autowired
	private RequestMethodServiceImpl requestMethodService;


	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

		String requestUrl = ((FilterInvocation) o).getRequestUrl();
		System.out.println(requestUrl);

		Integer method = RequestMethodUtil.requestMethodMap.get(((FilterInvocation) o).getHttpRequest().getMethod().toLowerCase());

		System.out.println( urlService.getRolesByUrl(requestUrl, method));
		List<String> needRoles = urlService.getRolesByUrl(requestUrl, method).stream().map(Role::getEnName).collect(Collectors.toList());

		System.out.println(needRoles);
		//List<RoleUrlMethod> roleUrlMethods = roleUrlMethodService.getRoleUrlMethodList();

		//List<String> allowRoles = new LinkedList<>();

		//for (RoleUrlMethod roleUrlMethod : roleUrlMethods) {
		//	if (antPathMatcher.match(roleUrlMethod.getUrl(), requestUrl) && RequestMethodUtil.isIncludeMethod(method, roleUrlMethod.getMethodIds())) {
		//		allowRoles.add(roleUrlMethod.getRoleEnName());
		//	}
		//}

		if (!needRoles.isEmpty()) {
			System.out.println("允许访问该请求的角色" + needRoles.toString());
			return SecurityConfig.createList(needRoles.toArray(String[]::new));
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
