package com.flowiee.app.service;

import com.flowiee.app.base.BaseService;
import com.flowiee.app.entity.Material;

import java.util.List;

public interface MaterialService extends BaseService<Material> {
    List<Material> findByCode(String code);

    List<Material> findByImportId(Integer importId);

    List<Material> findByUnit(Integer unitId);
}