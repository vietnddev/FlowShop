package com.flowiee.pms.modules.system.service;

import java.util.List;
import java.util.Map;

import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import org.springframework.data.domain.Page;

public interface CategoryService {
    Category findEntById(Long pId, boolean pThrowException);

    CategoryDTO findById(Long pId, boolean pThrowException);

    CategoryDTO save(CategoryDTO pDto);

    CategoryDTO update(CategoryDTO pEntity, Long pId);

    String delete(Long pId);

    List<Category> findRootCategory();

    Page<Category> findSubCategory(CATEGORY categoryType, Long parentId, List<Long> ignoreIds, int pageSize, int pageNum);

    List<Category> findUnits();

    List<Category> findColors();

    List<Category> findSizes();

    List<Category> findSalesChannels();

    List<Category> findPaymentMethods();

    List<Category> findOrderStatus(Long ignoreId);

    List<Category> findLedgerGroupObjects();

    List<Category> findLedgerReceiptTypes();

    List<Category> findLedgerPaymentTypes();

    boolean categoryInUse(Long categoryId);

    Map<CATEGORY, List<Category>> findByType(List<CATEGORY> categoryTypeList);
}