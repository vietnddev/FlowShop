package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.sales.dto.CustomerDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductReviewDTO extends BaseDTO implements Serializable {
    private ProductDTO product;
    private String reviewContent;
    private Integer rating;
    private CustomerDTO customer;
}