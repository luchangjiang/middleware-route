package com.lotstock.eddid.route.interceptor;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lotstock.eddid.route.util.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

/**
 * 拦截器
 * @author David
 *
 */
public class RouteInterceptor implements HandlerInterceptor{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 在请求处理之前进行调用（Controller方法调用之前）
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String url=request.getRequestURI();
		logger.info("请求ip: "+request.getRemoteAddr()+", url: "+url + ",参数param: " + getParams(request));
		if("/error".equals(url)){
			return false;
		}
		return true;
	}
	
	/**
	 * 数据响应回包
	 */
	protected void writeResponse(HttpServletResponse response, ResultModel result) {
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(JSON.toJSONString(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getParams(HttpServletRequest request) {
		try {
			String value = "";
			Enumeration<String> enu = request.getParameterNames();
			while (enu.hasMoreElements()) {
				String paraName = (String) enu.nextElement();
				value += paraName + "=" + request.getParameter(paraName) + "&";
			}
			return value;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,Object handler, ModelAndView modelAndView) throws Exception {
		
	}
	
	/**
	 * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
