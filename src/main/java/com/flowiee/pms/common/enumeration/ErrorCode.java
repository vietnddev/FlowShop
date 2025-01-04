package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SYSTEM_ERROR(1080, "System error. Please contact support."),
    SYSTEM_BUSY(1081, "System busy, please try later!"),
    DATA_ALREADY_EXISTS(1998, "Data already exists"),
    GET_ERROR_OCCURRED(1999, "An error occurred while get %s"),
    SEARCH_ERROR_OCCURRED(2000, "An error occurred while search %s"),
    CREATE_ERROR_OCCURRED(2001, "An error occurred while create %s"),
    UPDATE_ERROR_OCCURRED(2002, "An error occurred while update %s"),
    DELETE_ERROR_OCCURRED(2003, "An error occurred while delete %s"),
    UNAUTHORIZED(2004, "Unauthorized"),
    ERROR_FORBIDDEN(2005, "You are not authorized to use this function!"),
    RESOURCE_NOT_FOUND( 2006, "The resource you are accessing dose not found!"),
    ERROR_DATA_LOCKED( 2007, "The resource is currently in use and cannot be update or delete at this time!"),
    ACCOUNT_LOCKED(2008, "Your account may have been locked due to multiple failed login attempts. Please contact support."),
    ENTITY_NOT_FOUND(2009, "{0} not found in database!"),
    InsufficientLeaveBalance(2010, "Insufficient leave balance"),
    NoApproverDefined(2011, "Must have approver, no approver defined. Please contact administrator."),
    ProductOutOfStock(2012, "Product {0} out of stock!"),
    FileDoesNotAllowUpload(2013, "System does not support .{0}!");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}