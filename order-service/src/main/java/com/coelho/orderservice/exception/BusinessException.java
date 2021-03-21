package com.coelho.orderservice.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class BusinessException extends RuntimeException {

    private final String key;

    private final HttpStatus httpStatus;

    public BusinessException(String key) {
        super(key);
        this.key = key;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String key, HttpStatus httpStatus) {
        super(key);
        this.key = key;
        this.httpStatus = httpStatus;
    }
}