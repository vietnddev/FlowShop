package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDescriptionDTO extends BaseDTO implements Serializable {
    private Long productId;
    private String description;
}