package com.example.demo.common.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class customException {
	
	@ExceptionHandler(commonException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handler(commonException e) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("message", e.getMessage());
		map.put("code", e.getStatus());
		
		return new ResponseEntity<>(map, e.getStatus());
    }
}
