package com.tenco.mybankapp.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.mybankapp.dto.SignInFormDto;
import com.tenco.mybankapp.dto.SignUpFormDto;
import com.tenco.mybankapp.handler.exception.CustomPageException;
import com.tenco.mybankapp.handler.exception.CustomRestfullException;
import com.tenco.mybankapp.repository.entity.User;
import com.tenco.mybankapp.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	

	@Autowired   //DI 처리
	private UserService userService;
	
	@Autowired
	private HttpSession session;
//	
//	public UserController(UserService userService) {
//		this.userService = userService;
//	}
//	
	
	//회원 가입 페이지 요청
	//http://localhost:80/user/sign-up
	
	@GetMapping("/sign-up")
	public String signUp() {
		return "user/signup";
		
	}
	
	//로그인 페이지 요청
	//http://localhost:80/user/sign-up
	@GetMapping("/sign-in")
	public String signIn() {
		return "user/signin";
		
	}

	
	/*
	 * 
	 * @param dto
	 * @return 리다이렉트 로그인 페이지
	 */
	
	
	//DTO -Object Mapper
	@PostMapping("/sign-up")
	   public String signUpProc(SignUpFormDto dto) {
	      //1. 유효성 검사
	      if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
	         throw new CustomRestfullException("username을 입력하세요", HttpStatus.BAD_REQUEST);
	      }
	      if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
	         throw new CustomRestfullException("password를 입력하세요", HttpStatus.BAD_REQUEST);
	      }
	      if(dto.getFullname() == null || dto.getFullname().isEmpty()) {
	         throw new CustomRestfullException("fullname을 입력하세요", HttpStatus.BAD_REQUEST);
	      }
	      
	      int resultRowCount = userService.signUp(dto);
	      
	      
	      //redirect부터 새로운 요청이 생기는 것 
	      return "redirect:/user/sign-in";
	   }
	   
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto dto) {
		//1. 유효성 검사
		if(dto.getUsername() ==null || dto.getUsername().isEmpty()) {
			throw new CustomPageException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword() ==null || dto.getPassword().isEmpty()) {
			throw new CustomPageException("password을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		//서비스 호출
		User principal = userService.signIn(dto);
		session.setAttribute("principal", principal);//세션에 저장
		
		System.out.println("principal"+principal.toString());

		return "redirect:/account/list";
		//제이세션아이디를 발급 하고 서버 자신이 들고 있음
	
	}
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/sign-in";
	}
}
	//회원가입 
	//1. 유효성 검사 
	//인증검사 후 유효성 검사를 한 다음에 서비스 한테 던져주면 됨

