package com.example.server.service.impl;

import com.example.server.pojo.LoginParam;
import com.example.server.service.LoginService;
import com.example.server.utils.JwtTokenUtil;
import com.example.server.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : whz
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Value("${jwt.tokenHead}")
	private String tokenHead;

	/**
	 * 登陆后返回token
	 */
	@Override
	public RespBean login(LoginParam loginParam) {
		String username = loginParam.getUsername();
		String password = loginParam.getPassword();

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
			return RespBean.error("用户名或密码不正确");
		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		//生成token
		String token = jwtTokenUtil.generateToken(userDetails);
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("token", token);
		tokenMap.put("tokenHead", tokenHead);
		return RespBean.success("登陆成功", tokenMap);
	}
}
