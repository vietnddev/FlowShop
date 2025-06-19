package com.flowiee.pms.modules.system.service;

public interface CategoryExportService {
    byte[] exportTemplate(String categoryType);

    byte[] exportData(String categoryType);
}