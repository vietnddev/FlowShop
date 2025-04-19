package com.flowiee.pms.modules.promotion.dto;

import com.flowiee.pms.modules.promotion.entity.PromotionApply;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionApplyDTO extends PromotionApply implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    Long promotionApplyId;
    Long promotionInfoId;
    String promotionInfoTitle;
    Long productId;
    String productName;
    String appliedAt;
    Integer appliedBy;
}