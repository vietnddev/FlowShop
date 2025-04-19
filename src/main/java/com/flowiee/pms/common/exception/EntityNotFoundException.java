package com.flowiee.pms.common.exception;

import com.flowiee.pms.common.base.exception.BaseException;
import com.flowiee.pms.common.enumeration.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;
    private static boolean isRedirectView = false;
    private static ErrorCode errorCode = ErrorCode.ENTITY_NOT_FOUND;

    public EntityNotFoundException(String message) {
        this(message, null);
    }

    public EntityNotFoundException(Throwable cause) {
        this(null, cause);
    }

    public EntityNotFoundException(String message, Throwable sourceException) {
        super(errorCode, new Object[]{}, message, null, sourceException, isRedirectView);
    }

    public EntityNotFoundException(Object[] errorMsgParameter, String message, Throwable sourceException) {
        super(errorCode, errorMsgParameter, message, null, sourceException, isRedirectView);
    }
}