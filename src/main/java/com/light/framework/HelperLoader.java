package com.light.framework;

import com.light.framework.helper.AopHelper;
import com.light.framework.helper.BeanHelper;
import com.light.framework.helper.ClassHelper;
import com.light.framework.helper.ControllerHelper;
import com.light.framework.helper.IocHelper;
import com.light.framework.util.ClassUtil;

/**
 * 加载响应的Helper类
 * @author lushaoqing
 * @since 1.0.0 
 */

public final class HelperLoader {
	
	public static void init(){
		//需要注意AopHelper要在IocHelper之前加载，因为首先需要通过AopHelper获取代理对象，然后才能通过IocHelper进行依赖注入
		Class<?>[] classList = {
			ClassHelper.class,
			BeanHelper.class,
			AopHelper.class,
			IocHelper.class,
			ControllerHelper.class,		
		};
		for(Class<?> cls : classList){
			ClassUtil.loadClass(cls.getName());
		}
	}
}
