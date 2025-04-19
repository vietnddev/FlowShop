package com.flowiee.pms.modules.promotion.model;

import com.flowiee.pms.modules.product.dto.ProductDTO;
import com.flowiee.pms.modules.promotion.dto.VoucherInfoDTO;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateVoucherReq {
    private String title;
    private String description;
    private String applicableObjects;
    private String voucherType;
    private Integer quantity;
    private Integer length;
    private Integer discount;
    private BigDecimal discountPriceMax;
    private String startTime;
    private String endTime;
    private Long[] applicableProducts;

    public VoucherInfoDTO toPromotionInfoDTO() {
        VoucherInfoDTO lvDTO = new VoucherInfoDTO();
        lvDTO.setTitle(title);
        lvDTO.setDescription(description);
        lvDTO.setApplicableObjects(applicableObjects);
        lvDTO.setVoucherType(voucherType);
        lvDTO.setQuantity(quantity);
        lvDTO.setLength(length);
        lvDTO.setDiscount(discount);
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
                .append("applicableObjects", applicableObjects)
                .append("voucherType", voucherType)
                .append("quantity", quantity)
                .append("length", length)
                .append("discount", discount)
                .append("discountPriceMax", discountPriceMax)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("applicableProducts", applicableProducts)
                .toString();
    }
}