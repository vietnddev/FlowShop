package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
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