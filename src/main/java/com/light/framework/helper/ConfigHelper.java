package com.light.framework.helper;

import java.util.Properties;

import com.light.framework.ConfigConstant;
import com.light.framework.util.PropsUtil;

/**
 * 属性文件助手类
 * @since 1.0.0
 * @author lushaoqing 
 */
public final class ConfigHelper {
	private static final Properties CONFIG_PROPS = PropsUtil.LoadProps(ConfigConstant.CONFIG_FILE);
	
	/**
	 * 获取JDBC驱动 
	 */
	public static String getJdbcDriver() {
		return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER);
	}
	
	/**
	 * 获取JDBC URL 
	 */
	public static String getJdbcUrl(){
		return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME);
	}
	
	/**
	 * 获取JDBC 密码 
	 */
	public static String getJdbcPassword(){
		return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD);
	}
	
	/**
	 * 获取应用基础包名 
	 */
	public static String getAppBasePackage(){
		return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE);
	}
	
	/**
	 * 获取JSP路径 
	 */
	public static String getAppJspPath(){
		return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH);
	}
	
	/**
	 * 获取应用静态资源路径 
	 */
	public static String getAppAssertPath(){
		return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSERT_PATH);
	}
	
	/**
	 * 获取应用文件上传限制 
	 */
	public static int getAppUploadLimit(){
		return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.APP_UPLOAD_LIMIT,10);
	}
	
	
}
