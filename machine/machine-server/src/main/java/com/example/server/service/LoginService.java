package com.example.server.service;

import com.example.server.pojo.LoginParam;
import com.example.server.utils.RespBean;

/**
 * @author : whz
 */
public interface LoginService {

	/**
	 * 登陆后返回token
	 */
	RespBean login(LoginParam loginParam);
}
