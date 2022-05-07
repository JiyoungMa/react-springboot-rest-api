package com.kdt.reactspringbootrestapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "DB error")
public class JdbcFailException extends RuntimeException {
    public JdbcFailException(String s) {
    }
}
