package com.flowiee.app.service;

import com.flowiee.app.base.BaseService;

import java.util.List;

import com.flowiee.app.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryService extends BaseService<Category> {
    List<Category> findAll();

    List<Category> findRootCategory();

    List<Category> findSubCategory(String categoryType, Integer parentId);

    Page<Category> findSubCategory(String categoryType, Integer parentId, int pageSize, int pageNum);

    List<Category> findSubCategory(List<String> categoryTypes);

    Category findSubCategoryDefault(String categoryType);

    List<Category> findSubCategoryUnDefault(String categoryType);

    Boolean categoryInUse(Integer categoryId);
    
    String importData(MultipartFile fileImport, String categoryType);

    byte[] exportTemplate(String categoryType);

    byte[] exportData(String categoryType);
}