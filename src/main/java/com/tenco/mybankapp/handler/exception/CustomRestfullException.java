package com.tenco.mybankapp.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomRestfullException extends RuntimeException {
   
   private HttpStatus status;
   
   public CustomRestfullException(String message, HttpStatus httpStatus) {
      super(message); //부모 생성자 호출 - 얘의 부모는 런타임익셉션
      this.status = httpStatus;
   
   }

}