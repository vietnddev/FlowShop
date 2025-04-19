package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.product.entity.ProductVariantExim;
import com.flowiee.pms.modules.inventory.dto.TicketImportDTO;
import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.entity.MaterialTemp;
import com.flowiee.pms.modules.inventory.entity.TicketImport;
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