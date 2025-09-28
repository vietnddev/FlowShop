package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsReq;
import org.springframework.data.domain.Page;

public interface TransactionGoodsService {
    Page<TransactionGoodsDTO> getTransactionGoods(TransactionGoodsReq pReq);

    TransactionGoods findEntById(Long pId, boolean pThrowException);

    TransactionGoodsDTO findDtoById(Long pId, boolean pThrowException);

    TransactionGoodsDTO createTransactionGoods(TransactionGoodsDTO transactionGoodsDto) throws Exception;
}