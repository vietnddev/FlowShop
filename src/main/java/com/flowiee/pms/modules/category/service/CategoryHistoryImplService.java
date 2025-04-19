package com.flowiee.pms.modules.category.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.category.entity.CategoryHistory;
import com.flowiee.pms.modules.category.dto.CategoryHistoryDTO;
import com.flowiee.pms.modules.category.repository.CategoryHistoryRepository;
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