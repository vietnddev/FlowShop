package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import com.flowiee.pms.entity.system.FileStorage;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProductComboDTO extends BaseDTO implements Serializable {
    private String comboName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amountDiscount = BigDecimal.ZERO;
    private String note;
    private List<FileStorage> listImages;
    private BigDecimal totalValue;
    private Integer quantity;
    private String status;
    private List<ProductVariantDTO> applicableProducts;
}