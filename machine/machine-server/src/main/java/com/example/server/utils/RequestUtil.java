package com.example.server.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : whz
 */
public class RequestUtil {
	public static String getRequest(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String line;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;// 返回结果字符串
		if (con.getResponseCode() == 200) {
			is = con.getInputStream();
			// 封装输入流is，并指定字符集
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			// 存放数据
			StringBuffer sbf = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sbf.append(temp);
				sbf.append("\r\n");
			}
			result = sbf.toString();
			return result;
		}
		return null;
	}
}
