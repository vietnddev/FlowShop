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
@Tag(name = "Transaction goods API", description = "Quản lý xuất nhập hàng")
@RequiredArgsConstructor
public class TransactionGoodsController extends BaseController {
    private final TransactionGoodsService transactionGoodsService;

    @GetMapping("/{tranType}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TransactionGoodsDTO>> getTransactions(@PathVariable("tranType") String pTranType,
                                                                        @RequestParam(value = "pageSize", defaultValue = Constants.DEFAULT_PSIZE) int pageSize,
                                                                        @RequestParam(value = "pageNum", defaultValue = Constants.DEFAULT_PNUM) int pageNum,
                                                                        @RequestParam(value = "storageId", required = false) Long warehouseId) {
        return AppResponse.paged(transactionGoodsService.getTransactionGoods(TransactionGoodsType.fromStr(pTranType), TransactionGoodsReq.builder()
                .pageNum(pageNum - 1)
                .pageSize(pageSize)
                .warehouse(new StorageDTO(warehouseId))
                .build()));
    }

    @PostMapping("/{tranType}/create")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TransactionGoodsDTO> createTransactionImport(@PathVariable("tranType") String pTranType, @RequestBody TransactionGoodsDTO pRequest) throws Exception {
        return AppResponse.success(switch (TransactionGoodsType.fromStr(pTranType)) {
            case IMPORT -> {
                pRequest.setTransactionType(TransactionGoodsType.IMPORT);
                yield transactionGoodsService.createImportTransaction(pRequest);
            }
            case EXPORT -> {
                pRequest.setTransactionType(TransactionGoodsType.EXPORT);
                yield transactionGoodsService.createExportTransaction(pRequest);
            }
        });
    }

    @PutMapping("/{tranType}/update/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TransactionGoodsDTO> updateTransaction(@PathVariable("tranType") String pTranType,
                                                              @RequestBody TransactionGoodsDTO pDto,
                                                              @PathVariable("id") Long pTranId) {
        return AppResponse.success(switch (TransactionGoodsType.fromStr(pTranType)) {
            case IMPORT -> transactionGoodsService.updateImportTransaction(pDto, pTranId);
            case EXPORT -> transactionGoodsService.updateExportTransaction(pDto, pTranId);
        });
    }

    @DeleteMapping("/{tranType}/delete/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<String> deleteTransaction(@PathVariable("tranType") String pTranType, @PathVariable("id") Long pTranId) {
        return AppResponse.success(switch (TransactionGoodsType.fromStr(pTranType)) {
            case IMPORT -> transactionGoodsService.deleteImportTransaction(pTranId);
            case EXPORT -> transactionGoodsService.deleteExportTransaction(pTranId);
        });
    }

    @GetMapping("/{id}/item")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TransactionGoodsItemDTO>> getTransactionItems(@PathVariable("id") Long pTranId) {
        return AppResponse.success(transactionGoodsService.getItems(pTranId));
    }

    @PostMapping("/{id}/item/add")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TransactionGoodsItem>> addTransactionItems(@PathVariable("id") Long pTranId, @RequestBody TransactionGoodsReq pRequest) throws Exception {
        return AppResponse.success(transactionGoodsService.addItems(pTranId, pRequest));
    }

    @PostMapping("/{id}/item/generate")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<String> generateTransactionItemsByOrderCode(@PathVariable("id") Long pTranId, @RequestBody TransactionGoodsDTO pRequest) throws Exception {
        return AppResponse.success(transactionGoodsService.generateItemsByOrderCode(pTranId, pRequest.getOrder().getCode()), "Generated successfully");
    }

    @GetMapping("/{transactionId}/image")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<FileStorage>> getUploadedImages(@PathVariable("transactionId") Long pTranId) {
        return AppResponse.success(transactionGoodsService.getUploadedImages(pTranId));
    }
}