package com.tenco.mybankapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller//IoC에 대상()
//@RequestMapping("/temp")//굳이 temp를 반복적으로 안써도됨
public class TestController {
	
	//GET
	//주소설계 - http://localhost:80/temp-test 
	@GetMapping("/temp-test")
	public String tempTest() {
		return "temp";
	}
	
	//GET
	//주소설계- http://localhost:80/temp/main-page
	
	@GetMapping("/main-page")
	public String tempMainPage() {
		return "main";
	}


}
