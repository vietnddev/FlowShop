package com.flowiee.pms.exception;

import com.flowiee.pms.base.BaseException;
import com.flowiee.pms.common.enumeration.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BaseException {
	@Serial
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		this(message, null, null, false);
	}

	public ResourceNotFoundException(String message, Class sourceClass, Throwable sourceException, boolean redirectErrorUI) {
		super(ErrorCode.ENTITY_NOT_FOUND, new Object[]{}, message, sourceClass, sourceException, redirectErrorUI);
	}

	public ResourceNotFoundException(Object[] errorMsgParameter, String message, Class sourceClass, Throwable sourceException, boolean redirectErrorUI) {
		super(ErrorCode.ENTITY_NOT_FOUND, errorMsgParameter, message, sourceClass, sourceException, redirectErrorUI);
	}
}