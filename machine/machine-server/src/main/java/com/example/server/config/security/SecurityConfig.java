package com.example.server.config.security;

import com.example.server.pojo.Employee;
import com.example.server.service.impl.EmployeeServiceImpl;
import com.example.server.service.impl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

/**
 * @author : whz
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private EmployeeServiceImpl employeeService;
	@Autowired
	private RoleServiceImpl roleService;
	@Autowired
	private CustomFilter customFilter;
	@Autowired
	private CustomUrlDecisionManager customUrlDecisionManager;
	@Autowired
	private RestAuthorizationEntryPoint restAuthorizationEntryPoint;
	@Autowired
	private RestfulAccessDeniedHandler restfulAccessDeniedHandler;


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService())
				.passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				.antMatchers(
						"/login",
						"/login.html",
						"/logout",
						"/css/**",
						"/empImg/**",
						"/index.html",
						"favicon.ico",
						"/doc.html",
						"/webjars/**",
						"/swagger-resources/**",
						"/v2/api-docs/**",
						"/captcha",
						"ws/**",
						"/machine/price/**"
				);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf()
				////??????JWT???????????????csrf
				.disable()
				//??????token, ?????????session
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.authorizeRequests()
				.anyRequest().authenticated()

				//??????????????????
				.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
					@Override
					public <O extends FilterSecurityInterceptor> O postProcess(O o) {
						o.setAccessDecisionManager(customUrlDecisionManager);
						o.setSecurityMetadataSource(customFilter);
						return o;
					}
				})

				//????????????
				.and()
				.headers()
				.cacheControl();

		http.formLogin()
				.loginPage("/login.html");

		//??????JWT?????????????????????, ??????jwtAuthenticationTokenFilter??????????????????UsernamePasswordAuthenticationFilter???????????????
		http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		//???????????????????????????
		http.exceptionHandling()
				.accessDeniedHandler(restfulAccessDeniedHandler)
				.authenticationEntryPoint(restAuthorizationEntryPoint);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			Employee employee = employeeService.getEmployeeByUsername(username);
			if (employee != null) {
				employee.setRoles(roleService.getRolesByEmployeeId(employee.getId()));
			}
			return employee;
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
		return new JwtAuthenticationTokenFilter();
	}

}
