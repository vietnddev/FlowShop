package com.flowiee.pms.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@JsonPropertyOrder({"success", "status", "message", "pagination", "data"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppResponse<T> implements Serializable {
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

    @JsonIgnore
    @Serial
    static final long serialVersionUID = 7702134516418120340L;

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