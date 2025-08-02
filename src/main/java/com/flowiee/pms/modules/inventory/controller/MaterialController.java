package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.dto.MaterialDTO;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.inventory.service.MaterialService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/storage/material")
@Tag(name = "Material API", description = "Quản lý nguyên vật liệu")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MaterialController extends BaseController {
    MaterialService mvMaterialService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Find all nguyên vật liệu")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleProduct.readMaterial(true)")
    public AppResponse<List<MaterialDTO>> findAll(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                               @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            if (pageSize == null) pageSize = -1;
            if (pageNum == null) pageNum = 1;
            Page<MaterialDTO> materials = mvMaterialService.find(pageSize, pageNum - 1, null, null, null, null, null, null);
            return mvCHelper.success(materials.getContent(), pageNum, pageSize, materials.getTotalPages(), materials.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "material"), ex);
        }
    }

    @Operation(summary = "Create new material")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleProduct.insertMaterial(true)")
    public AppResponse<MaterialDTO> insert(@RequestBody MaterialDTO materialDTO) {
        try {
            return mvCHelper.success(mvMaterialService.save(materialDTO));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "material"), ex);
        }
    }
}