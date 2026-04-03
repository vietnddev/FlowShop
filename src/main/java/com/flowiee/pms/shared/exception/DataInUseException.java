package com.flowiee.pms.shared.exception;

import com.flowiee.pms.shared.base.BaseException;
import com.flowiee.pms.shared.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class DataInUseException extends BaseException {
    public DataInUseException(String message) {
        this(message, null, null, false);
    }

    public DataInUseException(String message, Throwable sourceException) {
        this(message, null, sourceException, false);
    }

    public DataInUseException(String message, Class sourceClass, Throwable sourceException, boolean redirectErrorUI) {
        super(ErrorCode.ACCOUNT_LOCKED, new Object[]{}, message, sourceClass, sourceException, redirectErrorUI);
    }
}