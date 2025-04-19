package com.flowiee.pms.modules.category.service;

public interface CategoryExportService {
    byte[] exportTemplate(String categoryType);

    byte[] exportData(String categoryType);
}