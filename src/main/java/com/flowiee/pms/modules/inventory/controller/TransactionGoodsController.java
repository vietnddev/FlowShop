package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.dto.StorageDTO;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsItemDTO;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsReq;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.media.entity.FileStorage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg/transaction-goods")
@Tag(name = "Transaction goods API", description = "Quản lý nhập hàng")
@RequiredArgsConstructor
public class TransactionGoodsController extends BaseController {
    private final TransactionGoodsService transactionGoodsService;

    @GetMapping("/import")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TransactionGoodsDTO>> getImportTransactions(@RequestParam(value = "pageSize", defaultValue = Constants.DEFAULT_PSIZE) int pageSize,
                                                                        @RequestParam(value = "pageNum", defaultValue = Constants.DEFAULT_PNUM) int pageNum,
                                                                        @RequestParam(value = "storageId", required = false) Long warehouseId) {
        return AppResponse.paged(transactionGoodsService.getTransactionGoods(TransactionGoodsType.IMPORT, TransactionGoodsReq.builder()
                .pageNum(pageNum - 1)
                .pageSize(pageSize)
                .warehouse(new StorageDTO(warehouseId))
                .build()));
    }

    @GetMapping("/export")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TransactionGoodsDTO>> getExportTransactions(@RequestParam(value = "pageSize", defaultValue = Constants.DEFAULT_PSIZE) int pageSize,
                                                                        @RequestParam(value = "pageNum", defaultValue = Constants.DEFAULT_PNUM) int pageNum,
                                                                        @RequestParam(value = "storageId", required = false) Long warehouseId) {
        return AppResponse.paged(transactionGoodsService.getTransactionGoods(TransactionGoodsType.EXPORT, TransactionGoodsReq.builder()
                .pageNum(pageNum - 1)
                .pageSize(pageSize)
                .warehouse(new StorageDTO(warehouseId))
                .build()));
    }

    @PostMapping("/import/create")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TransactionGoodsDTO> createTransactionImport(@RequestBody TransactionGoodsDTO pRequest) throws Exception {
        pRequest.setTransactionType(TransactionGoodsType.IMPORT);
        return AppResponse.success(transactionGoodsService.createImportTransaction(pRequest));
    }

    @GetMapping("/{id}/item")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TransactionGoodsItemDTO>> getTransactionImportItems(@PathVariable("id") Long pTranId) {
        return AppResponse.success(transactionGoodsService.getImportItems(pTranId));
    }

    @PostMapping("/import/{id}/item/add")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TransactionGoodsItem>> addTransactionImportItems(@PathVariable("id") Long pTranId, @RequestBody TransactionGoodsReq pRequest) throws Exception {
        return AppResponse.success(transactionGoodsService.addImportItems(pTranId, pRequest));
    }

    @PutMapping("/import/update/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TransactionGoodsDTO> updateTransactionImport(@RequestBody TransactionGoodsDTO pDto, @PathVariable("id") Long pTranId) {
        return AppResponse.success(transactionGoodsService.updateImportTransaction(pDto, pTranId));
    }

    @GetMapping("/{transactionId}/image")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<FileStorage>> getUploadedImagesImport(@PathVariable("transactionId") Long pTranId) {
        return AppResponse.success(transactionGoodsService.getUploadedImagesImport(pTranId));
    }
}