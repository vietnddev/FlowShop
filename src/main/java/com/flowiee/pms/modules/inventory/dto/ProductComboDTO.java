package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.media.entity.FileStorage;
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