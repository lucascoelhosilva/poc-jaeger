package com.coelho.orderservice.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends BusinessException {

    public NotFoundException(String key) {
        super(key, HttpStatus.NOT_FOUND);
    }

}