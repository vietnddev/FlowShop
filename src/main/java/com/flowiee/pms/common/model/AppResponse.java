package com.flowiee.pms.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;

@JsonPropertyOrder({"success", "status", "message", "pagination", "data"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppResponse<T> implements Serializable {
    @JsonProperty("success")
    Boolean success;

    @JsonProperty("status")
    HttpStatus status;

    @JsonProperty("message")
    String message;

    @JsonProperty("data")
    T data;

    @JsonProperty("pagination")
    PaginationModel pagination;

    public AppResponse(Boolean success, HttpStatus status, String message, T data, PaginationModel pagination) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    public static <T> AppResponse<T> success(@NonNull T data) {
        return success(data, "OK");
    }

    public static <T> AppResponse<T> success(@NonNull T data, String message) {
        return success(data, message, null);
    }

    public static <T> AppResponse<List<T>> paged(Page<T> page) {
        Pageable pageable = page.getPageable();
        if (pageable.isPaged()) {
            return success(page.getContent(), pageable.getPageNumber() + 1, pageable.getPageSize(), page.getTotalPages(), page.getTotalElements());
        }
        return success(page.getContent());
    }

    public static <T> AppResponse<T> success(@NonNull T data, int pageNum, int pageSize, int totalPage, long totalElements) {
        return success(data, "OK", pageNum, pageSize, totalPage, totalElements);
    }

    public static <T> AppResponse<T> success(@NonNull T data, String message, int pageNum, int pageSize, int totalPage, long totalElements) {
        return success(data, message, new AppResponse.PaginationModel(pageNum, pageSize, totalPage, totalElements));
    }

    public static <T> AppResponse<T> success(@NonNull T data, String message, AppResponse.PaginationModel pagination) {
        return new AppResponse<>(true, HttpStatus.OK, message, data, pagination);
    }

    public static <T> AppResponse<T> fail(@NonNull HttpStatus httpStatus) {
        return fail(httpStatus, "NOK");
    }

    public static <T> AppResponse<T> fail(@NonNull HttpStatus httpStatus, @NonNull String message) {
        return new AppResponse<>(false, httpStatus, message, null, null);
    }

    public static AppResponse<String> deleted() {
        return deleted("Deleted successfully");
    }

    public static <T> AppResponse<T> deleted(@NonNull T data) {
        return success(data, "OK");
    }

    public static <T> AppResponse<T> badRequest(String message) {
        return new AppResponse<>(false, HttpStatus.BAD_REQUEST, message, null, null);
    }

    public static <T> AppResponse<T> internalServer(String message) {
        return internalServer(message, null);
    }

    public static <T> AppResponse<T> internalServer(String message, Throwable trace) {
        if (trace != null) {
            trace.printStackTrace();
        }
        return new AppResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR, message, null, null);
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaginationModel {
        int pageNum;
        int pageSize;
        int totalPage;
        long totalElements;

        public PaginationModel(int pageNum, int pageSize, int totalPage, long totalElements) {
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.totalPage = totalPage;
            this.totalElements = totalElements;
        }

        @Override
        public String toString() {
            return "Paged{" +
                    "pageNum=" + pageNum +
                    ", pageSize=" + pageSize +
                    ", totalPage=" + totalPage +
                    ", totalElements=" + totalElements +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AppResponse{" +
                "success=" + success +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", pagination=" + pagination +
                '}';
    }
}