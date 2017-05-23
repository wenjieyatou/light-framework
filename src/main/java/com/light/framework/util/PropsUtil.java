package com.light.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
* @ClassName: PropsUtil
* @Description: 属性文件工具类
* @author BEE 
*/
public final class PropsUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PropsUtil.class);
	
	/**
	* @Title: LoadProps
	* @Description: 加载属性文件
	* @param @param fileName
	* @param @return    设定文件
	* @return Properties    返回类型
	* @throws
	*/
	public static Properties LoadProps(String fileName){
		Properties props = null;
		InputStream is = null;
		try{
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			if(is ==  null){
				throw new FileNotFoundException(fileName + " file is not found");
			}
			props = new Properties();
			props.load(is);
		}catch (Exception e) {
			LOGGER.error("Load properties file failure",e);
		}finally{
			if(is != null){
				try{
					is.close();
				}catch (IOException e) {
					LOGGER.error("close input stream failure",e);
				}
			}
		}
		return props;
	}
	
	/**
	* @Title: getString
	* @Description: 获取字符属性（默认值为空字符串）
	* @param @param props
	* @param @param key
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	public static String getString(Properties props,String key){
		return getString(props,key,"");
	}
	
	/**
	* @Title: getString
	* @Description: 获取字符型属性（可指定默认值）
	* @param @param props
	* @param @param key
	* @param @param defaultValue
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	public static String getString(Properties props,String key,String defaultValue){
		String value = defaultValue;
		if(props.containsKey(key)){
			value = props.getProperty(key);
		}
		return value;
	}
	
	public static int getInt(Properties props,String key){
		return getInt(props, key,0);
	}
	
	public static int getInt(Properties props,String key,int defaultValue){
		int value = defaultValue;
		if(props.containsKey(key)){
			value = CastUtil.castInt(props.getProperty(key));
		}
		return value;
	}
	
	public static boolean getBoolean(Properties props,String key){
		return getBoolean(props, key,false);
	}
	
	public static boolean getBoolean(Properties props,String key,boolean defaultValue){
		boolean value = defaultValue;
		if(props.containsKey(key)){
			value = CastUtil.castBoolean(props.getProperty(key));
		}
		return value;
	}
}