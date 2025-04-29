package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.dto.OrderDTO;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface OrderPrintInvoiceService {
    void printInvoicePDF(OrderDTO pOrder, List<Integer> pOrderIds, boolean isExportAll, HttpServletResponse response);
}