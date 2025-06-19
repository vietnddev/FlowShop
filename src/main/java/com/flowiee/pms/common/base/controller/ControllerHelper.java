package com.flowiee.pms.common.base.controller;

import com.flowiee.pms.common.model.AppResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ControllerHelper {
    public <T> AppResponse<T> success(@NonNull T data) {
        return success(data, "OK");
    }

    public <T> AppResponse<T> success(@NonNull T data, String message) {
        return success(data, message, null);
    }

    public <T> AppResponse<?> paged(Page<T> page) {
        return success(page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements());
    }

    public <T> AppResponse<T> success(@NonNull T data, int pageNum, int pageSize, int totalPage, long totalElements) {
        return success(data, "OK", pageNum, pageSize, totalPage, totalElements);
    }

    public <T> AppResponse<T> success(@NonNull T data, String message, int pageNum, int pageSize, int totalPage, long totalElements) {
        return success(data, message, new AppResponse.PaginationModel(pageNum, pageSize, totalPage, totalElements));
    }

    public <T> AppResponse<T> success(@NonNull T data, String message, AppResponse.PaginationModel pagination) {
        return new AppResponse<>(true, HttpStatus.OK, message, data, pagination);
    }

    public <T> AppResponse<T> fail(@NonNull HttpStatus httpStatus) {
        return fail(httpStatus, "NOK");
    }

    public <T> AppResponse<T> fail(@NonNull HttpStatus httpStatus, @NonNull String message) {
        return new AppResponse<>(false, httpStatus, message, null, null);
    }

    public AppResponse<String> deleted() {
        return deleted("Deleted successfully");
    }

    public <T> AppResponse<T> deleted(@NonNull T data) {
        return success(data, "OK");
    }

    public <T> AppResponse<T> badRequest(String message) {
        return new AppResponse<>(false, HttpStatus.BAD_REQUEST, message, null, null);
    }

    public <T> AppResponse<T> internalServer(String message) {
        return new AppResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR, message, null, null);
    }

    public <T> AppResponse<T> internalServer(String message, Throwable trace) {
        if (trace == null) {
            return internalServer(message);
        }
        trace.printStackTrace();
        return new AppResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR, message, null, null);
    }
}