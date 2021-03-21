package com.coelho.orderservice.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
public class IntegrationException extends BusinessException{

    public IntegrationException(String key, HttpStatus status) {
        super(key, status);
    }
}
