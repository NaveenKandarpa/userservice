package com.example.userservice.exceptionhandlers;

import com.example.userservice.dtos.TokenNotFoundExceptionDto;
import com.example.userservice.exceptions.TokenNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<TokenNotFoundExceptionDto> handleTokenNotFoundException() {
        TokenNotFoundExceptionDto dto = new TokenNotFoundExceptionDto();
        dto.setMessage("Invalid token. Please enter a valid token");
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

}
