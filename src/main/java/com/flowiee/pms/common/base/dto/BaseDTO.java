package com.flowiee.pms.common.base.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseDTO implements Serializable {
    protected Long id;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    protected Long createdBy;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lastUpdatedAt;
    private String lastUpdatedBy;
    //@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    //private LocalDateTime deletedAt;
    //private String deletedBy;
}