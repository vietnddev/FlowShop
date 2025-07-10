package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class ItemsDTO extends BaseDTO implements Serializable {
    private Long cartId;
    private ProductVariantDTO productDetail;
    private Long itemId;
    private String itemName;
    private int quantity;
    private String note;
    //private OrderCartDTO orderCart;
    private String priceType;
    private BigDecimal price;
    private BigDecimal priceOriginal;
    private BigDecimal extraDiscount;
    private Long productVariantId;
    private BigDecimal subTotal;

    @Override
    public String toString() {
        return "ItemsDTO{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", note='" + note + '\'' +
                '}';
    }
}