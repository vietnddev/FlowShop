package com.flowiee.pms.common.base.exception;

import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private ErrorCode  errorCode;
    private Object[]   errorMsgParameter;
    private String     message;
    private Class      sourceClass;
    private Throwable  sourceException;
    private boolean    isRedirectView;
    private String     view;
    private HttpStatus httpStatus;

    public BaseException() {}

    public BaseException(ErrorCode errorCode, Object[] errorMsgParameter, String message, Class sourceClass, Throwable sourceException, boolean isRedirectView) {
        super(CoreUtils.isNullStr(message) ? errorCode.getDescription() : message, sourceException);
        this.errorCode = errorCode;
        this.errorMsgParameter = errorMsgParameter;
        this.message = message;
        this.sourceClass = sourceClass;
        this.sourceException = sourceException;
        this.isRedirectView = isRedirectView;
        setHttpStatus(getHttpStatusByErrorCode());
        setMessage(getDisplayMessage());
    }

    public String getDisplayMessage() {
        String lvPrefixCode = "[" + errorCode.getCode() + "] ";
        String lvMessage = CoreUtils.isNullStr(message) ? errorCode.getDescription() : message;
        if (errorMsgParameter != null && errorMsgParameter.length > 0) {
            for (int i = 0; i < errorMsgParameter.length; i++) {
                if (lvMessage.contains("{" + i + "}")) {
                    lvMessage = lvMessage.replace("{" + i + "}", errorMsgParameter[i].toString());
                }
            }
        }
        return lvPrefixCode + lvMessage;
    }

    public String getFullStackTrace() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printStackTrace(pw);
        return sw.toString();
    }

    private HttpStatus getHttpStatusByErrorCode() {
        HttpStatus httpStatus = null;

        if (errorCode.equals(ErrorCode.ACCOUNT_LOCKED)) {
            httpStatus = HttpStatus.LOCKED;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return httpStatus;
    }
}