package com.flowiee.pms.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.LOCKED)
public class DataInUseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DataInUseException(String message) {
        super(message);
    }

    public DataInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}