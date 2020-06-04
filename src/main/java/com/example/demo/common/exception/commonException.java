package com.example.demo.common.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class commonException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private HttpStatus status;
	private String message;

	public commonException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
