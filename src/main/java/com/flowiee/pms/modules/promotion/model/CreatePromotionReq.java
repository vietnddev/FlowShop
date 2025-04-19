package com.flowiee.pms.modules.promotion.model;

import com.flowiee.pms.modules.product.dto.ProductDTO;
import com.flowiee.pms.modules.promotion.dto.PromotionInfoDTO;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreatePromotionReq {
    private String title;
    private String description;
    private Integer discountPercent;
    private BigDecimal discountPrice;
    private BigDecimal discountPriceMax;
    private String startTime;
    private String endTime;
    private Long[] applicableProducts;

    public PromotionInfoDTO toPromotionInfoDTO() {
        PromotionInfoDTO lvDTO = new PromotionInfoDTO();
        lvDTO.setTitle(title);
        lvDTO.setDescription(description);
        lvDTO.setDiscountPercent(discountPercent);
        lvDTO.setDiscountPrice(discountPrice);
        lvDTO.setDiscountPriceMax(discountPriceMax);
        lvDTO.setStartTimeStr(startTime);
        lvDTO.setEndTimeStr(endTime);

        List<ProductDTO> lvProducts = new ArrayList<>();
        for (Long lvProductId : applicableProducts) {
            ProductDTO lvProduct = new ProductDTO();
            lvProduct.setId(lvProductId);
            lvProducts.add(lvProduct);
        }
        lvDTO.setApplicableProducts(lvProducts);

        return lvDTO;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", title)
                .append("description", description)
                .append("discountPercent", discountPercent)
                .append("discountPrice", discountPrice)
                .append("discountPriceMax", discountPriceMax)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("applicableProducts", applicableProducts)
                .toString();
    }
}