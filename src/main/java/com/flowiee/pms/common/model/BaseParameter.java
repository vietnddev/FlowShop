package com.flowiee.pms.common.model;

import com.flowiee.pms.common.constants.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class BaseParameter {
    private int pageNum = -1;
    private int pageSize = -1;
}