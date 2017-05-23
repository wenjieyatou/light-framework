package com.light.framework;

/**
 * 返回数据对象
 * @since 1.0.0
 * @author lushaoqing 
 */
public class Data {
	
	/**
	 * 模型数据 
	 */
	private Object model;
	
	public Data(Object model){
		this.model = model;
	}

	public Object getModel() {
		return model;
	}
	
}
