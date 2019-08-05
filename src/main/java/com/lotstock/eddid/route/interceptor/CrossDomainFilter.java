package com.lotstock.eddid.route.interceptor;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 跨域设置
 * 
 * @author XIONGWEI
 *
 */
@Component
public class CrossDomainFilter extends OncePerRequestFilter {
	
	protected static final Logger logger = Logger.getLogger(CrossDomainFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		// 这里填写你允许进行跨域的主机ip
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		// 允许的访问方法
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
		// Access-Control-Max-Age 用于 CORS 相关配置的缓存
		httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
		httpServletResponse.setHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, token");

		// 如果是OPTIONS请求，直接返回200
		if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		filterChain.doFilter(request, response);
	}
}
