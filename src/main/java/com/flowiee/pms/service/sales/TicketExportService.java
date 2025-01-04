package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.entity.sales.TicketExport;
import org.springframework.data.domain.Page;

public interface TicketExportService extends BaseCurdService<TicketExport> {
    Page<TicketExport> findAll(int pageSize, int pageNum, Long storageId);

    TicketExport save(OrderDTO orderDTO);

    TicketExport createDraftTicketExport(long storageId, String title, String orderCode);
}