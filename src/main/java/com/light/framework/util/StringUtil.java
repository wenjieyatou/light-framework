package com.light.framework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类 
 */
public final class StringUtil {
	
	/**
	 * 字符串分隔符
	 */
	public static final String SEPARATOR = String.valueOf((char) 29);
	
	/**
	 * 判断字符串是否为空 
	 */
	public static boolean isEmpty(String str){
		if(str != null){
			str = str.trim();
		}
		return StringUtils.isEmpty(str);
	}
	
	/**
	 * 判断字符串是否非空 
	 */
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	/**
	 * 用指定的标识符分割字符串 
	 */
	public static String[] splitString(String body, String separator) {
		if(StringUtil.isNotEmpty(separator) && StringUtil.isNotEmpty(body)){
			String[] result = body.split(separator);
			return result;
		}else{
			return null;
		}
	}
}
