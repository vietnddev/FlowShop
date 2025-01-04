package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.product.ProductVariantExim;
import com.flowiee.pms.model.dto.TicketImportDTO;
import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.entity.product.MaterialTemp;
import com.flowiee.pms.entity.sales.TicketImport;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TicketImportService extends BaseCurdService<TicketImport> {
    Page<TicketImport> findAll(int pageSize, int pageNum, String text, Long supplierId, Long paymentMethod, String payStatus, String importStatus, Long storageId);

    TicketImport findDraftImportPresent(Long createdBy);

    TicketImport createDraftTicketImport(TicketImportDTO ticketImportDTO);

    TicketImport updateStatus(Long entityId, String status);

    List<ProductVariantExim> addProductToTicket(Long ticketImportId, List<Long> productVariantIds);

    List<MaterialTemp> addMaterialToTicket(Long ticketImportId, List<Long> materialIds);

    void restockReturnedItems(Long pStorageId, String pOrderCode);
}