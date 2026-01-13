package com.vozhe.jwt.exceptions;


import com.vozhe.jwt.payload.BaseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandlerControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<BaseResult> handleException(UserExistsException exception){
        BaseResult errorResponse = new BaseResult();
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setDescription(exception.getMessage());
        errorResponse.setCode("01");
        return new ResponseEntity<BaseResult>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<BaseResult> handleException(Exception ex){
        BaseResult errorResponse = new BaseResult();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setDescription(ex.getMessage());
        errorResponse.setCode("01");
        return new ResponseEntity<BaseResult>(errorResponse,HttpStatus.BAD_REQUEST);
    }
}
