package com.flowiee.pms.exception;

import com.flowiee.pms.base.exception.BaseException;
import com.flowiee.pms.common.enumeration.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.CONFLICT)
public class DataExistsException extends BaseException {
	@Serial
	private static final long serialVersionUID = 1L;
	private static ErrorCode errorCode = ErrorCode.DATA_ALREADY_EXISTS;

	public DataExistsException(String message) {
		this(message, null);
	}

	public DataExistsException(Throwable cause) {
		this(null, cause);
	}

	public DataExistsException(String message, Throwable sourceException) {
		super(errorCode, new Object[]{}, message, null, sourceException, false);
	}

	public DataExistsException(Object[] errorMsgParameter, String message, Throwable sourceException) {
		super(errorCode, errorMsgParameter, message, null, sourceException, false);
	}
}