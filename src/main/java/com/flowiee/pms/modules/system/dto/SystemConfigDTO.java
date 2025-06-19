package com.flowiee.pms.modules.system.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class SystemConfigDTO extends BaseDTO implements Serializable {
    private String code;
    private String name;
    private String value;
    private Integer sort;
}