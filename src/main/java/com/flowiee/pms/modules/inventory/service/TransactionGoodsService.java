package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsItemDTO;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsReq;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionGoodsService {
    Page<TransactionGoodsDTO> getTransactionGoods(TransactionGoodsReq pReq);

    TransactionGoods findEntById(Long pId, boolean pThrowException);

    TransactionGoodsDTO findDtoById(Long pId, boolean pThrowException);

    TransactionGoodsDTO createImportTransaction(TransactionGoodsDTO transactionGoodsDto) throws Exception;

    List<TransactionGoodsItemDTO> getImportItems(Long pTranId);

    List<TransactionGoodsItem> addImportItems(Long pTranId, TransactionGoodsReq pReq);
}