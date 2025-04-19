package com.flowiee.pms.modules.product.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.product.entity.GiftRedemption;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GiftCatalogDTO extends BaseDTO implements Serializable {
    private String name;
    private String description;
    private Integer requiredPoints;
    private Integer stock;
    private Boolean isActive;
    private List<GiftRedemption> giftRedemptionList;
}