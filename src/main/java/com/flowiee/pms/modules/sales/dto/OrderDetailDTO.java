package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.inventory.util.ProductVariantConvert;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderDetailDTO extends OrderDetail implements Serializable {
    private Long orderId;
    private ProductVariantDTO productVariantDTO;
    private ProductVariantDTO item;
    private Boolean isReturned;

    public static OrderDetailDTO fromOrderDetail(OrderDetail pInput) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(pInput.getId());
        dto.setOrder(pInput.getOrder());
        dto.setOrderId(pInput.getOrder().getId());
        dto.setProductDetail(pInput.getProductDetail());
        dto.setProductVariantDTO(ProductVariantConvert.toDto(pInput.getProductDetail()));
        dto.setPriceType(pInput.getPriceType());
        dto.setQuantity(pInput.getQuantity());
        dto.setPrice(pInput.getPrice());
        dto.setPriceOriginal(pInput.getPriceOriginal());
        dto.setExtraDiscount(pInput.getExtraDiscount());
        dto.setNote(ObjectUtils.isNotEmpty(pInput.getNote()) ? pInput.getNote() : "");
        dto.setIsReturned(Boolean.TRUE.equals(pInput.getIsReturned()));
        dto.setStatus(pInput.isStatus());
        return dto;
    }

    public static List<OrderDetailDTO> fromOrderDetails(List<OrderDetail> pItems) {
        if (CollectionUtils.isEmpty(pItems)) {
            return new ArrayList<>();
        }
        return pItems.stream()
                .map(OrderDetailDTO::fromOrderDetail)
                .toList();
    }
}