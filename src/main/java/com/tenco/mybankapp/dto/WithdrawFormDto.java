package com.tenco.mybankapp.dto;

import lombok.Data;

@Data
public class WithdrawFormDto {	
	private Long amount;
	private String wAccountNumber;
	private String password;
}
