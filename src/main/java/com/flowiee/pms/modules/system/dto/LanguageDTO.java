package com.flowiee.pms.modules.system.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class LanguageDTO extends BaseDTO implements Serializable {
    private String module;
    private String screen;
    private String code;
    private String key;
    private String value;
}