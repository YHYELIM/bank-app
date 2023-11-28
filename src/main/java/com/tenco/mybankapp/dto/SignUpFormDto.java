package com.tenco.mybankapp.dto;

import lombok.Data;

@Data
public class SignUpFormDto {
	//jsp에 넘기는 데이터를 여기에다가 담는다
	private String username;
	private String password;
	private String fullname;
	

}
