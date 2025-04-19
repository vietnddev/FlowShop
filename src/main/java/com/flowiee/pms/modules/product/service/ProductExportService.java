package com.flowiee.pms.modules.product.service;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductExportService {
    ResponseEntity<?> exportToExcel(Integer pProductId, List<Integer> pProductIds, boolean isExportAll);

    byte[] exportToCSV(Long pProductId, List<Long> pProductIds, boolean isExportAll);

    byte[] exportToPDF(Long pProductId, List<Integer> pProductIds, boolean isExportAll);
}