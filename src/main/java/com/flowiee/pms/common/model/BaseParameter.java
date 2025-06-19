package com.flowiee.pms.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseParameter {
    private int pageNum = -1;
    private int pageSize = -1;
}