package com.example.server.config.security;

import com.example.server.utils.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
public class RestfulAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("application/json");
		PrintWriter out = httpServletResponse.getWriter();
		RespBean respBean = RespBean.error("权限不足，请联系管理员");
		respBean.setCode(403);
		out.write(new ObjectMapper().writeValueAsString(respBean));
		out.flush();
		out.close();
	}
}
