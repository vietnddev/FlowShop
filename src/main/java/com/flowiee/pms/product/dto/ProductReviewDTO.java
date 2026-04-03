package com.flowiee.pms.product.dto;

import com.flowiee.pms.shared.base.BaseDTO;
import com.flowiee.pms.customer.dto.CustomerDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductReviewDTO extends BaseDTO implements Serializable {
    private ProductDTO product;
    private String reviewContent;
    private Integer rating;
    private CustomerDTO customer;
}