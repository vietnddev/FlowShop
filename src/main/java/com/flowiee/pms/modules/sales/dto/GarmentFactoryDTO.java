package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.product.dto.ProductDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GarmentFactoryDTO extends BaseDTO implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String address;
    private String note;
    private String status;
    private List<ProductDTO> productList;
}