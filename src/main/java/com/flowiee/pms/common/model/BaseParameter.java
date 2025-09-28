package com.flowiee.pms.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class BaseParameter {
    private int pageNum = -1;
    private int pageSize = -1;
}