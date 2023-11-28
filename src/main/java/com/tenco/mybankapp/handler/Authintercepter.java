package com.tenco.mybankapp.handler;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.mybankapp.handler.exception.UnAuthorizedException;
import com.tenco.mybankapp.repository.entity.User;
import com.tenco.mybankapp.utils.Define;

//만드는 방법
//1. HandlerInterceptor 구현 IoC 대상

@Component //IoC 대상 - 싱글톤 관리 //스프링컨테이너 메모리에 올림
public class Authintercepter implements HandlerInterceptor{
	
	//컨트롤러에 들어오기 전에 동작
	//controller-->true(들어감), false(안들어감)
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		//세션에 사용자 정보 확인
	HttpSession session	= request.getSession();
		//웹브라우저에서 요청이 들어왔을때 리퀘스트 객체에 세션이 들어가있음
	User principal =(User) session.getAttribute(Define.PRINCIPAL);//원래 princial 오브젝트 타입인데 유저로 다운캐스팅
		if(principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		return true;
	}
	
	//뷰가 렌더링 되기 전에 호출 되는 메서드
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	//요청 처리가 완료된 후, 즉, 뷰 렌더링이 완료된 후에 호출 되는 메서드
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	

}
