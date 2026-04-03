package com.flowiee.pms.order.service;

import com.flowiee.pms.order.dto.OrderDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface OrderPrintInvoiceService {
    void printInvoicePDF(OrderDTO pOrder, List<Integer> pOrderIds, boolean isExportAll, HttpServletResponse response);
}