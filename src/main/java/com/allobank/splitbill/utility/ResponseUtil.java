package com.allobank.splitbill.utility;

import com.allobank.splitbill.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<ApiResponse<T>> generateSuccessResponse(String message, T data, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(true, message, data, null), status);
    }
    public static <T> ResponseEntity<ApiResponse<T>> generateErrorResponse(String message, Object error, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(false, message, null, error), status);
    }
}
