package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductReviewDTO extends BaseDTO implements Serializable {
    private ProductDTO product;
    private String reviewContent;
    private Integer rating;
    private CustomerDTO customer;
}