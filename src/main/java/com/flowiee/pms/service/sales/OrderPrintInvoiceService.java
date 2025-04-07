package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.model.dto.OrderDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface OrderPrintInvoiceService {
    void printInvoicePDF(OrderDTO pOrder, List<Integer> pOrderIds, boolean isExportAll, HttpServletResponse response);
}