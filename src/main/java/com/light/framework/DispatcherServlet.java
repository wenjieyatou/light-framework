package com.light.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.light.framework.bean.Handler;
import com.light.framework.bean.Param;
import com.light.framework.bean.View;
import com.light.framework.helper.BeanHelper;
import com.light.framework.helper.ConfigHelper;
import com.light.framework.helper.ControllerHelper;
import com.light.framework.helper.RequestHelper;
import com.light.framework.helper.ServletHelper;
import com.light.framework.helper.UploadHelper;
import com.light.framework.util.ArrayUtil;
import com.light.framework.util.CodeUtil;
import com.light.framework.util.JsonUtil;
import com.light.framework.util.ReflectionUtil;
import com.light.framework.util.StreamUtil;
import com.light.framework.util.StringUtil;

/**
 * 请求转发器
 * @author lushaoqing
 * @since 1.0.0 
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

	@Override
	public void init(ServletConfig serveltConfig) throws ServletException {
		//初始化相关Helper类，实例化各种Bean，解析注解
		HelperLoader.init();
		//获取ServletContext 对象（用于注册Servlet)
		ServletContext servletContext = serveltConfig.getServletContext();
		//注册处理JSP 的 Servlet
		ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
		String appJspPath = ConfigHelper.getAppJspPath() + "*";
		jspServlet.addMapping(appJspPath);
		//注册处理静态资源的默认Servlet
		ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
		defaultServlet.addMapping(ConfigHelper.getAppAssertPath() + "*");
		//初始化文件上传助手
		UploadHelper.init(servletContext);
	}
	
	
	public void dservice(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
		//获取请求方法与请求路径
		String requestMethod = request.getMethod().toUpperCase();
		String requestPath = request.getPathInfo();
		//获取Action处理器
		Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
		if(handler != null){
			//获取Controller类及其Bean实例
			Class<?> controllerClass = handler.getControllerClass();
			Object controllerBean = BeanHelper.getBean(controllerClass);
			//System.out.println(controllerBean.getClass().getName());
			//创建请求参数对象
			Map<String, Object> paramMap = new HashMap<String,Object>();
			Enumeration<String> paramNames = request.getParameterNames();
			while(paramNames.hasMoreElements()){
				String paramName = paramNames.nextElement();
				String paramValue = request.getParameter(paramName);
				paramMap.put(paramName, paramValue);
			}
			String body = CodeUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
			if(StringUtil.isNotEmpty(body)){
				String []params = StringUtil.splitString(body, "&");
				if(ArrayUtil.isNotEmpty(params)){
					for(String param : params){
						String[] array = StringUtil.splitString(param,"=");
						if(ArrayUtil.isNotEmpty(array) && array.length == 2){
							String paramName = array[0];
							String paramValue = array[1];
							paramMap.put(paramName, paramValue);
						}
					}

				}
			}
			Param param = new Param(paramMap);
			//调用Action方法
			Method actionMethod = handler.getActionMethod();
			//System.out.println(actionMethod);
			Object result;
			// 这里需对 param  处理，
			if(paramMap != null && paramMap.size() > 0){
				result = ReflectionUtil.invokedMethod(controllerBean, actionMethod, param);
			}else{
				result = ReflectionUtil.invokedMethod(controllerBean, actionMethod);
			}
			//处理Action方法返回值
			if(result instanceof View){
				//返回JSP页面
				View view = (View) result;
				String path = view.getPath();
				if(StringUtil.isEmpty(path)){
					if(path.startsWith("/")){
						response.sendRedirect(request.getContextPath() + path);
					}else{
						Map<String,Object> model = view.getModel();
						for(Map.Entry<String, Object>entry : model.entrySet()){
							request.setAttribute(entry.getKey(), entry.getValue());
						}
						request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
					}
				} else if(result instanceof Data){
					//返回JSON数据
					Data data = (Data)result;
					Object model = data.getModel();
					if(model != null){
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						PrintWriter writer = response.getWriter();
						String json = JsonUtil.toJson(model);
						writer.write(json);
						writer.flush();
						writer.close();
					}
				}
			}

		}
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletHelper.init(request, response);
		try{
			//获取请求方法与请求路径 ，获取客户机的请求方式
			String requestMethod = request.getMethod();
			String requestPath = request.getPathInfo();
			
			if(requestPath.equals("/favicon.ico")){
				return;
			}
			
			//获取Action 处理器
			Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
			if(handler != null){
				//获取Controller类极其Bean实例
				Class<?> controllerClass = handler.getControllerClass();
				Object controllerBean = BeanHelper.getBean(controllerClass);
				
				Param param;
				if(UploadHelper.isMultipart(request)){
					param = UploadHelper.createParam(request);
				}else{
					param = RequestHelper.createParam(request);
				}
				
				Object result;
				Method actionMethod = handler.getActionMethod();
				if(param.isEmpty()){
					result = ReflectionUtil.invokedMethod(controllerBean, actionMethod);
				}else{
					result = ReflectionUtil.invokedMethod(controllerBean, actionMethod, param);
				}
				
				if(result instanceof View){
					handleViewResult((View)result, request, response);
				}else if( result instanceof Data){
					handleDataResult((Data) result,response);
				}
			}
		}finally{
			ServletHelper.destory();
		}
	}
	private void handleViewResult(View view,HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException{
		String path = view.getPath();
		if(StringUtil.isNotEmpty(path)){
			if(path.startsWith("/")){
				response.sendRedirect(request.getContextPath() + path);
			}else{
				Map<String,Object> model = view.getModel();
				for(Map.Entry<String, Object> entry : model.entrySet()){
					request.setAttribute(entry.getKey(), entry.getValue());
				}
				request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);			}
		}
	}
	
	private void handleDataResult(Data data,HttpServletResponse response)throws IOException{
		Object model = data.getModel();
		if(model != null){
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			String json = JsonUtil.toJson(model);
			writer.write(json);
			writer.flush();
			writer.close();
		}
	}
}
