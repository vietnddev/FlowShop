package com.flowiee.pms.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flowiee.pms.shared.base.CreateService;
import com.flowiee.pms.shared.base.DeleteService;
import com.flowiee.pms.shared.base.FindService;
import com.flowiee.pms.shared.base.UpdateService;
import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.system.enums.CATEGORY;
import com.flowiee.pms.system.dto.CategoryDTO;
import org.springframework.data.domain.Page;

public interface CategoryService extends FindService<CategoryDTO>, CreateService<CategoryDTO>, UpdateService<CategoryDTO>, DeleteService {
    Category findEntById(Long pId, boolean pThrowException);

    List<Category> findRootCategory();

    Page<Category> findSubCategory(CATEGORY categoryType, Long parentId, List<Long> ignoreIds, int pageSize, int pageNum);

    List<Category> findByType(CATEGORY pType);

    boolean categoryInUse(Long categoryId);

    Map<CATEGORY, List<Category>> findByType(List<CATEGORY> categoryTypeList);

    Map<CATEGORY, Category> findByIdsAsMap(Set<Long> ids);
}