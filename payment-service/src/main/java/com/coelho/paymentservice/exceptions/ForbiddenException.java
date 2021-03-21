package com.coelho.paymentservice.exceptions;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
public class ForbiddenException extends BusinessException {

    public ForbiddenException(String key) {
        super(key, HttpStatus.FORBIDDEN);
    }

}