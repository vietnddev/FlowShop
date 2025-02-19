package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.base.system.Core;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.OrderDetailRpt;
import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.model.dto.OrderDetailDTO;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.OrderPrintInvoiceService;
import com.flowiee.pms.service.sales.GenerateQRCodeService;
import com.flowiee.pms.service.sales.OrderReadService;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.utils.ReportUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderPrintInvoiceServiceImpl extends BaseService implements OrderPrintInvoiceService {
    OrderReadService mvOrderReadService;
    GenerateQRCodeService mvGenerateQRCodeService;

    @Override
    public void printInvoicePDF(Order pOrder, List<Integer> pOrderIds, boolean isExportAll, HttpServletResponse response) {
        OrderDTO lvOrderDto = OrderDTO.fromOrder(pOrder);

        boolean checkBatch = false;

        //ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        //PDFMergerUtility mergePdf = new PDFMergerUtility();
        //Barcode_Image.createImage(order.getId().toString() + ".png", order.getId().toString());
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("customerName", lvOrderDto.getReceiverName());
        parameterMap.put("customerAddress", lvOrderDto.getReceiverAddress());
        parameterMap.put("customerPhone", lvOrderDto.getReceiverPhone());
        parameterMap.put("customerEmail", lvOrderDto.getReceiverEmail());
        parameterMap.put("totalSubtotal","$ 0");
        parameterMap.put("totalShippingCost","$ 0");
        parameterMap.put("discount","$ " + lvOrderDto.getAmountDiscount());
        parameterMap.put("totalPayment", lvOrderDto.getTotalAmountDiscount());
        parameterMap.put("paymentMethod", lvOrderDto.getPayMethodName());
        parameterMap.put("invoiceNumber", lvOrderDto.getCode());
        parameterMap.put("orderDate", lvOrderDto.getOrderTime());
        parameterMap.put("nowDate", new Date());
        FileStorage f = mvGenerateQRCodeService.findQRCodeOfOrder(lvOrderDto.getId());
        if (f != null) {
            Path barcodePath = Path.of(Core.getResourceUploadPath() + FileUtils.getImageUrl(f, true));
            if (barcodePath.toFile().exists()) {
                parameterMap.put("barcode", barcodePath);
            }
        } else {
            parameterMap.put("barcode", null);
        }
        parameterMap.put("logoPath", FileUtils.logoPath);

        // orderDetails
        List<OrderDetailRpt> listDetail = new ArrayList<>();
        for (OrderDetailDTO detailDTO : lvOrderDto.getListOrderDetailDTO()) {
            BigDecimal lvUnitPrice = detailDTO.getPrice();
            int lvQuantity = detailDTO.getQuantity();
            listDetail.add(OrderDetailRpt.builder()
                    .productName(detailDTO.getProductVariantDTO().getVariantName())
                    .unitPrice(lvUnitPrice)
                    .quantity(lvQuantity)
                    .subTotal(lvUnitPrice.multiply(BigDecimal.valueOf(lvQuantity)))
                    .note(detailDTO.getNote())
                    .build());
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            try {
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.font.name", "false");
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", " inline; filename=deliveryNote" + lvOrderDto.getId() + ".pdf");
                InputStream reportStream = new FileInputStream(ReportUtils.getReportTemplate("Invoice"));
                JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameterMap, new JRBeanCollectionDataSource(listDetail));
                JasperExportManager.exportReportToPdfStream(jasperPrint, (checkBatch) ? byteArrayOutputStream : servletOutputStream);
                if (checkBatch) {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                } else {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                }
            } catch (Exception e) {
                // display stack trace in the browser
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                response.setContentType("text/plain");
                response.getOutputStream().print(stringWriter.toString());
                throw new AppException(e);
            }
        } catch (Exception e) {
            throw new AppException("Exception print PDF: " + e.getMessage());
        }
    }
}