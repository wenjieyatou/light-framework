package com.light.framework.helper;

import java.lang.reflect.Field;

import java.util.Map;

import com.light.framework.util.ArrayUtil;
import com.light.framework.util.CollectionUtil;
import com.light.framework.util.ReflectionUtil;
import com.ligth.framework.annotation.Inject;

/**
 * 依赖注入助手类
 * @author lushaoqing
 * @since 1.0.0 
 */

public final class IocHelper {
	static{
		//获取所有的Bean类与Bean实例之间的映射关系（简称 Bean Map）
		Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();
		if(CollectionUtil.isNotEmpty(beanMap)){
			//遍历Bean Map
			for(Map.Entry<Class<?>, Object>beanEntry : beanMap.entrySet()){
				//从BeanMap 中获取Bean类与Bean实例
				Class<?> beanClass = beanEntry.getKey();
				Object beanInstance = beanEntry.getValue();
				//获取Bean 类定义的所有成员变量（简称Bean Field）
				Field[] beanFields = beanClass.getDeclaredFields();
				if(ArrayUtil.isNotEmpty(beanFields)){
					//遍历Bean Field
					for(Field beanField : beanFields){
						//判断当前Bean Field 是否 带有 Inject 注解
						if(beanField.isAnnotationPresent(Inject.class)){
							//System.out.println("当前方法"+beanField);
							//在Bean Map中获取Bean Field对应的实例
							Class<?> beanFieldClass = beanField.getType();
							Object beanFieldInstance = beanMap.get(beanFieldClass);
							if(beanFieldInstance != null){
								//通过反射初始化BeanField的值 将某属性值设置到对应的实例化对象中
								ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
							}
						}
					}
				}
			}
		}
	}
}
