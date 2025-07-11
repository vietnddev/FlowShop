package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.inventory.util.ProductVariantConvert;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailDTO extends OrderDetail implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    Long orderId;
    ProductVariantDTO productVariantDTO;
    ProductVariantDTO item;

    public static OrderDetailDTO fromOrderDetail(OrderDetail d) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(d.getId());
        dto.setOrder(d.getOrder());
        dto.setOrderId(d.getOrder().getId());
        dto.setProductDetail(d.getProductDetail());
        dto.setProductVariantDTO(ProductVariantConvert.toDto(d.getProductDetail()));
        dto.setPriceType(d.getPriceType());
        dto.setQuantity(d.getQuantity());
        dto.setPrice(d.getPrice());
        dto.setPriceOriginal(d.getPriceOriginal());
        dto.setExtraDiscount(d.getExtraDiscount());
        dto.setNote(ObjectUtils.isNotEmpty(d.getNote()) ? d.getNote() : "");
        dto.setStatus(d.isStatus());
        return dto;
    }

    public static List<OrderDetailDTO> fromOrderDetails(List<OrderDetail> listDetails) {
        List<OrderDetailDTO> list = new ArrayList<>();
        if (listDetails != null && !listDetails.isEmpty()) {
            for (OrderDetail o : listDetails) {
                list.add(OrderDetailDTO.fromOrderDetail(o));
            }
        }
        return list;
    }
}