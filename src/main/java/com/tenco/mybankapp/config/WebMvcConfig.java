package com.tenco.mybankapp.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.mybankapp.handler.Authintercepter;

// @Configuration -> 스프링 부트 설정 클래스 이다 의미
@Configuration // IoC 등록 --> 2개 이상 IoC 등록 처리 
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private Authintercepter authintercepter;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(authintercepter)
		.addPathPatterns("/account/**")
		//url 주소가 /account/패턴일때 인터셉터 발동
		.addPathPatterns("/auth/**");
	
		
	}
	

}
