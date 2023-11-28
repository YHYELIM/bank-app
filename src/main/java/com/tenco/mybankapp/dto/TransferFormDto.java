package com.tenco.mybankapp.dto;

import lombok.Data;

@Data
public class TransferFormDto {

	private Long amount;
	private String dAccountNumber;
	private String wAccountNumber;
	private String password;
	
	
	//이체 금액
	//입금 계좌 
	//출금 계좌 
	
	
}
