package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.modules.product.entity.MaterialTemp;
import com.flowiee.pms.modules.product.entity.ProductVariantExim;
import com.flowiee.pms.modules.inventory.entity.TicketImport;
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
public class TicketImportDTO extends TicketImport implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    Long supplierId;
    String supplierName;
    Long paymentMethodId;
    String paymentMethodName;
    Long storageId;
    String                   storageName;
    List<ProductVariantExim> listProductVariantTemp;
    List<MaterialTemp>       listMaterialTemp;

    public static TicketImportDTO fromTicketImport(TicketImport ticketImport) {
        TicketImportDTO dto = new TicketImportDTO();
        dto.setId(ticketImport.getId());
        dto.setTitle(ticketImport.getTitle());
        if (ticketImport.getSupplier() != null) {
            dto.setSupplierId(ticketImport.getSupplier().getId());
            dto.setSupplierName(ticketImport.getSupplier().getName());
        }
        if (ticketImport.getPaymentMethod() != null) {
            dto.setPaymentMethodId(ticketImport.getPaymentMethod().getId());
            dto.setPaymentMethodName(ticketImport.getPaymentMethod().getName());
        }
        if (ticketImport.getStorage() != null) {
            dto.setStorageName(ticketImport.getStorage().getName());
        }
        dto.setStorage(ticketImport.getStorage());
        dto.setImporter(ticketImport.getImporter());
        dto.setImportTime(ticketImport.getImportTime());
        dto.setNote(ticketImport.getNote() != null ? ticketImport.getNote() : "");
        dto.setStatus(ticketImport.getStatus());
        //dto.setListProductDTO(ProductVariantDTO.fromProductVariants(ticketImport.getListProductDetails()));
        dto.setListProductVariantTemp(ticketImport.getListProductVariantTemps());
        //dto.setListMaterialDTO(MaterialDTO.fromMaterials(ticketImport.getListMaterials()));
        dto.setListMaterialTemp(ticketImport.getListMaterialTemps());
        dto.setTotalItems(ticketImport.getTotalItems());
        dto.setTotalValue(ticketImport.getTotalValue());
        return dto;
    }

    public static List<TicketImportDTO> fromTicketImports(List<TicketImport> ticketImports) {
        List<TicketImportDTO> list = new ArrayList<>();
        for (TicketImport t : ticketImports) {
            list.add(TicketImportDTO.fromTicketImport(t));
        }
        return list;
    }
}