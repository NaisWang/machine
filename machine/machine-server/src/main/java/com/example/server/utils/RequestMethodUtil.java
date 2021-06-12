package com.example.server.utils;

import com.example.server.pojo.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : whz
 */
public class RequestMethodUtil {

	private static final Map<String, Integer> requestMethodMap = new HashMap<>();

	static {
		requestMethodMap.put("get", 1);
		requestMethodMap.put("post", 2);
		requestMethodMap.put("delete", 3);
		requestMethodMap.put("put", 4);
		requestMethodMap.put("head", 5);
	}

	/**
	 * 判断参数method的值是否在methodIds对应的method中
	 *
	 * @param method
	 * @param methodIds
	 * @return
	 */
	public static boolean isIncludeMethod(String method, String methodIds) {
		if (!requestMethodMap.containsKey(method.toLowerCase())) {
			return true;
		}
		return methodIds.contains(requestMethodMap.get(method.toLowerCase()).toString());
	}

}
