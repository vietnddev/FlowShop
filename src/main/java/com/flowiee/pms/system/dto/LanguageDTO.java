package com.flowiee.pms.system.dto;

import com.flowiee.pms.shared.base.BaseDTO;
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