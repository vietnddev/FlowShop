package com.flowiee.pms.modules.inventory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.inventory.entity.ProductVariantExim;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.entity.Order;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketExportDTO extends BaseDTO implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    StorageDTO storage;
    String title;
    String exporter;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime exportTime;
    String note;
    String status;
    List<Order> listOrders;
    List<ProductVariantExim> listProductVariantTemp;
    List<FileStorage> listImages;
    Integer totalItems;
    BigDecimal totalValue;
    String storageName;
    String exportTimeStr;

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
        //dto.setStorage(t.getStorage());
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