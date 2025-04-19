package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.modules.product.entity.ProductVariantExim;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketExportDTO extends TicketExport implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    List<OrderDTO>           listOrderDTO;
    List<ProductVariantExim> listProductTemp;

    public static TicketExportDTO fromTicketExport(TicketExport t) {
        TicketExportDTO dto = new TicketExportDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setExporter(t.getExporter());
        dto.setExportTime(t.getExportTime());
        dto.setNote(t.getNote());
        dto.setStatus(t.getStatus());
        dto.setListOrderDTO(OrderDTO.fromOrders(t.getListOrders()));
        dto.setStorage(t.getStorage());
        if (dto.getStorage() != null) {
            dto.setStorageName(dto.getStorage().getName());
        }
        return dto;
    }

    public static List<TicketExportDTO> fromTickerExports(List<TicketExport> ts) {
        List<TicketExportDTO> list = new ArrayList<>();
        for (TicketExport t : ts) {
            list.add(TicketExportDTO.fromTicketExport(t));
        }
        return list;
    }
}