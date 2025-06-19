package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.system.entity.CategoryHistory;

import java.util.List;
import java.util.Map;

public interface CategoryHistoryService {
    List<CategoryHistory> save(Map<String, Object[]> logChanges, String title, Long categoryId);
}