package com.tenco.mybankapp.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomPageException extends RuntimeException {
  
   private HttpStatus httpStatus;
   
   public CustomPageException(String message, HttpStatus status) {
      super(message);
      this.httpStatus = status;
      
   }
}