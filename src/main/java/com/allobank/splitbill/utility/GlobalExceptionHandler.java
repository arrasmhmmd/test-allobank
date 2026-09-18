package com.allobank.splitbill.utility;

import com.allobank.splitbill.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception exception) {
        return ResponseUtil.generateErrorResponse("An unexpected error occurred", exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
