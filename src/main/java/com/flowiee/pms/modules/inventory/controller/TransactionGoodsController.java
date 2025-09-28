package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.prefix}/stg/transaction-goods")
@Tag(name = "Transaction goods API", description = "Quản lý nhập hàng")
@RequiredArgsConstructor
public class TransactionGoodsController extends BaseController {
    private final TransactionGoodsService transactionGoodsService;

    @PostMapping("/import/create")
    public AppResponse<TransactionGoodsDTO> createTransactionImport(@RequestBody TransactionGoodsDTO pRequest) throws Exception {
        return AppResponse.success(transactionGoodsService.createTransactionGoods(pRequest));
    }
}