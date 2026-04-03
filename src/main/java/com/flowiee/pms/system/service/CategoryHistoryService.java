package com.flowiee.pms.system.service;

import com.flowiee.pms.system.entity.CategoryHistory;

import java.util.List;
import java.util.Map;

public interface CategoryHistoryService {
    List<CategoryHistory> save(Map<String, Object[]> logChanges, String title, Long categoryId);
}