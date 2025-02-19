package com.flowiee.pms.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ReportDTO implements Serializable {
    private String reportId;
    private String reportName;
    private Object data;
}