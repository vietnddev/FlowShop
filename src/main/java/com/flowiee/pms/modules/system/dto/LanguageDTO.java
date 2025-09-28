package com.flowiee.pms.modules.system.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class LanguageDTO extends BaseDTO implements Serializable {
    private String messageKey;
    private String messageValue;
    private String module;
    private String page;
    private String componentType;
    private String locale;
}