package com.flowiee.pms.model;

import com.flowiee.pms.common.enumeration.QueryOperator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class Filter {
    private String field;
    private QueryOperator operator;
    private String value;
    private List<String> values;
}