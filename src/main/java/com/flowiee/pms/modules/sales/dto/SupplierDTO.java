package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SupplierDTO extends BaseDTO implements Serializable {
    private String name;
    private String code;
    private String taxCode;
    private String phone;
    private String email;
    private String address;
    private String website;
    private String contactPoint;
    private String productProvided;
    private BigDecimal currentDebtAmount;
    private String note;
    private String status;
    //private List<TicketImportDTO> listTicketImportGoods;
    //private List<MaterialDTO> listMaterial;
    //private List<ProductDTO> productList;

    public SupplierDTO(Long id, String name) {
        setId(id);
        this.name = name;
    }
}