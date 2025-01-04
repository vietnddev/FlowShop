package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.base.service.BaseExportService;
import com.flowiee.pms.service.sales.OrderReadService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderExportServiceImpl extends BaseExportService {
    private final OrderReadService mvOrderReadService;

    private DateTimeFormatter DF_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);

        List<OrderDTO> listData = mvOrderReadService.findAll();
        for (int i = 0; i < listData.size(); i++) {
            OrderDTO orderDTO = listData.get(i);
            XSSFRow row = sheet.createRow(i + 4);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(orderDTO.getCode());
            row.createCell(2).setCellValue(orderDTO.getOrderTime().format(DF_DDMMYYYY));
            row.createCell(3).setCellValue(orderDTO.getSalesChannelName());
            row.createCell(4).setCellValue(String.valueOf(orderDTO.getTotalAmountDiscount()));
            row.createCell(5).setCellValue(orderDTO.getPayMethodName());
            row.createCell(6).setCellValue(orderDTO.getPaymentStatus() ? "Đã thanh toán" : "Chưa thanh toán");
            row.createCell(7).setCellValue(orderDTO.getCustomerName());
            row.createCell(8).setCellValue(orderDTO.getReceiverPhone());
            row.createCell(9).setCellValue(orderDTO.getReceiverAddress());
            row.createCell(10).setCellValue(orderDTO.getOrderStatusName());
            row.createCell(11).setCellValue(orderDTO.getNote());

            setBorderCell(row, 0, 11);
        }
    }
}