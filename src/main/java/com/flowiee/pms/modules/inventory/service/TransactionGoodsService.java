package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import org.springframework.data.domain.Page;

public interface TransactionGoodsService {
    Page<TransactionGoodsDTO> getTransactionGoods(int page, int size, String type, String transactionFromDate, String transactionToDate,
                                                  String transactionDate, String transactionCode, String orderCode, String itemCode,
                                                  String createBy, String[] sortColumn, String[] sortType) throws Exception;

    TransactionGoodsDTO createTransactionGoods(TransactionGoodsDTO transactionGoodsDto) throws Exception;
}