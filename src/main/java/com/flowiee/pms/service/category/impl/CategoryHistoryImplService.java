package com.flowiee.pms.service.category.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.category.CategoryHistory;
import com.flowiee.pms.model.dto.CategoryHistoryDTO;
import com.flowiee.pms.repository.category.CategoryHistoryRepository;
import com.flowiee.pms.service.category.CategoryHistoryService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryHistoryImplService extends BaseGService<CategoryHistory, CategoryHistoryDTO, CategoryHistoryRepository> implements CategoryHistoryService {

    public CategoryHistoryImplService(CategoryHistoryRepository pCategoryHistoryRepository) {
        super(CategoryHistory.class, CategoryHistoryDTO.class, pCategoryHistoryRepository);
    }

    @Override
    public List<CategoryHistory> save(Map<String, Object[]> logChanges, String title, Long categoryId) {
        List<CategoryHistory> categoryHistories = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : logChanges.entrySet()) {
            String field = entry.getKey();
            String oldValue = ObjectUtils.isNotEmpty(entry.getValue()[0]) ? entry.getValue()[0].toString() : " ";
            String newValue = ObjectUtils.isNotEmpty(entry.getValue()[1]) ? entry.getValue()[1].toString() : " ";

            CategoryHistory categoryHistory = CategoryHistory.builder()
                    .title(title)
                    .category(new Category(categoryId, null))
                    .field(field)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build();

            categoryHistories.add(mvEntityRepository.save(categoryHistory));
        }
        return categoryHistories;
    }
}