package com.example.server.config.security;

import com.example.server.utils.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author : whz
 */
@Component
public class RestAuthorizationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("application/json");
		PrintWriter out = httpServletResponse.getWriter();
		RespBean respBean = RespBean.error("未登录，请登陆");
		respBean.setCode(401);
		out.write(new ObjectMapper().writeValueAsString(respBean));
		out.flush();
		out.close();
	}
}
