package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import com.flowiee.pms.entity.sales.Customer;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerContactDTO extends BaseDTO implements Serializable {
    Customer customer;
    String code;
    String value;
    String note;
    String isDefault;
    boolean status;
    boolean isUsed;
}