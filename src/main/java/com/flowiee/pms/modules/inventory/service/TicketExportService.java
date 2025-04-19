package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import org.springframework.data.domain.Page;

public interface TicketExportService extends BaseCurdService<TicketExport> {
    Page<TicketExport> findAll(int pageSize, int pageNum, Long storageId);

    TicketExport save(OrderDTO orderDTO);

    TicketExport createDraftTicketExport(long storageId, String title, String orderCode);
}