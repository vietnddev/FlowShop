package com.flowiee.pms.controller.storage;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.EximModel;
import com.flowiee.pms.model.StorageItems;
import com.flowiee.pms.model.dto.StorageDTO;
import com.flowiee.pms.service.ExportService;
import com.flowiee.pms.service.storage.StorageService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.TemplateExport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/storage")
@Tag(name = "Storage API", description = "Storage management")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StorageController extends BaseController {
    StorageService mvStorageService;
    @Autowired
    @NonFinal
    @Qualifier("storageExportServiceImpl")
    ExportService  mvExportService;

    @Operation(summary = "Find all storages")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public AppResponse<List<StorageDTO>> findStorages(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                      @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            if (pageSize == null || pageNum == null) {
                return success(mvStorageService.findAll());
            }
            Page<StorageDTO> storagePage = mvStorageService.findAll(pageSize, pageNum - 1);
            return success(storagePage.getContent(), pageNum, pageSize, storagePage.getTotalPages(), storagePage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Find detail storage")
    @GetMapping("/{storageId}")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public AppResponse<StorageDTO> findDetailStorage(@PathVariable("storageId") Long storageId) {
        StorageDTO storage = mvStorageService.findById(storageId, true);
        return success(storage);
    }

    @Operation(summary = "Create storage")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleStorage.insertStorage(true)")
    public AppResponse<StorageDTO> createStorage(StorageDTO storageDTO) {
        try {
            return success(mvStorageService.save(storageDTO));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Update storage")
    @PutMapping("/update/{storageId}")
    @PreAuthorize("@vldModuleStorage.updateStorage(true)")
    public AppResponse<StorageDTO> updateStorage(@RequestBody StorageDTO storage, @PathVariable("storageId") Long storageId) {
        try {
            return success(mvStorageService.update(storage, storageId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Delete storage")
    @DeleteMapping("/delete/{storageId}")
    @PreAuthorize("@vldModuleStorage.deleteStorage(true)")
    public AppResponse<String> deleteStorage(@PathVariable("storageId") Long storageId) {
        return success(mvStorageService.delete(storageId));
    }

    @Operation(summary = "Find detail storage")
    @GetMapping("/{storageId}/items")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public AppResponse<List<StorageItems>> findStorageItems(@PathVariable("storageId") Long storageId,
                                                            @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                            @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                            @RequestParam(value = "searchText", required = false) String searchText) {
        try {
            Page<StorageItems> storageItemsPage = mvStorageService.findStorageItems(pageSize, pageNum -1, storageId, searchText);
            return success(storageItemsPage.getContent(), pageNum, pageSize, storageItemsPage.getTotalPages(), storageItemsPage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Export storage information")
    @GetMapping("/export/{storageId}")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public ResponseEntity<InputStreamResource> exportData(@PathVariable("storageId") Long storageId) {
        EximModel model = mvExportService.exportToExcel(TemplateExport.EX_STORAGE_ITEMS, new Storage(storageId), false);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }
}