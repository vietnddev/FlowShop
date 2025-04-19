package com.flowiee.pms.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorModel {
    private int status;
    private String message;
    private long timestamp;

    public ErrorModel(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}