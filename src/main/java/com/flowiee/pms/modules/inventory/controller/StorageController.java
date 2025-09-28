package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.modules.inventory.model.StorageItems;
import com.flowiee.pms.modules.inventory.dto.StorageDTO;
import com.flowiee.pms.modules.system.service.ExportService;
import com.flowiee.pms.modules.inventory.service.StorageService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.TemplateExport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StorageController extends BaseController {
    private final StorageService mvStorageService;
    @Autowired
    @Qualifier("storageExportServiceImpl")
    private ExportService mvStorageExportService;
    @Autowired
    @Qualifier("transactionGoodsExportServiceImpl")
    private ExportService mvTransactionGoodsExportService;

    @Operation(summary = "Find all storages")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public AppResponse<List<StorageDTO>> findStorages(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                      @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            if (pageSize == null || pageNum == null) {
                return AppResponse.success(mvStorageService.find(-1, -1).getContent());
            }
            Page<StorageDTO> storagePage = mvStorageService.find(pageSize, pageNum - 1);
            return AppResponse.paged(storagePage);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Find detail storage")
    @GetMapping("/{storageId}")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public AppResponse<StorageDTO> findDetailStorage(@PathVariable("storageId") Long storageId) {
        StorageDTO storage = mvStorageService.findById(storageId, true);
        return AppResponse.success(storage);
    }

    @Operation(summary = "Create storage")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleStorage.insertStorage(true)")
    public AppResponse<StorageDTO> createStorage(@RequestBody StorageDTO storageDTO) {
        try {
            return AppResponse.success(mvStorageService.save(storageDTO));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Update storage")
    @PutMapping("/update/{storageId}")
    @PreAuthorize("@vldModuleStorage.updateStorage(true)")
    public AppResponse<StorageDTO> updateStorage(@RequestBody StorageDTO storage, @PathVariable("storageId") Long storageId) {
        try {
            return AppResponse.success(mvStorageService.update(storage, storageId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Delete storage")
    @DeleteMapping("/delete/{storageId}")
    @PreAuthorize("@vldModuleStorage.deleteStorage(true)")
    public AppResponse<String> deleteStorage(@PathVariable("storageId") Long storageId) {
        return AppResponse.success(mvStorageService.delete(storageId));
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
            return AppResponse.success(storageItemsPage.getContent(), pageNum, pageSize, storageItemsPage.getTotalPages(), storageItemsPage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "storage"), ex);
        }
    }

    @Operation(summary = "Export storage information")
    @GetMapping("/export/{storageId}")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public ResponseEntity<InputStreamResource> exportData(@PathVariable("storageId") Long storageId,
                                                          @RequestParam(value = "src", required = false) String pSrc) {
        String lvSrc = CoreUtils.trim(pSrc);
        EximResult model = null;
        switch (lvSrc) {
            case "inventory":
                model = mvStorageExportService.exportToExcel(TemplateExport.EX_STORAGE_ITEMS, new StorageDTO(storageId), false);
                break;
            case "inbound":
                model = mvTransactionGoodsExportService.exportToExcel(TemplateExport.EX_STORAGE_TRANS_GOODS,
                        TransactionGoodsDTO.builder()
                                .warehouse(new StorageDTO(storageId))
                                .transactionType(TransactionGoodsType.IMPORT)
                                .build(),
                        false);
                break;
            case "outbound":
                model = mvTransactionGoodsExportService.exportToExcel(TemplateExport.EX_STORAGE_TRANS_GOODS,
                        TransactionGoodsDTO.builder()
                                .warehouse(new StorageDTO(storageId))
                                .transactionType(TransactionGoodsType.EXPORT)
                                .build(),
                        false);
                break;
            default:
                return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }
}