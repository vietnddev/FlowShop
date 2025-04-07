package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDamagedDTO extends BaseDTO implements Serializable {
    private ProductDTO product;
    private ProductVariantDTO productVariant;
    private Integer quantity;
    private String damageReason;
    private LocalDateTime recordedDate;
    private List<FileDTO> imageList;
    private String note;
}