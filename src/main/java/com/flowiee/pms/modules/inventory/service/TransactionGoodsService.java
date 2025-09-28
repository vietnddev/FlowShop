package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsItemDTO;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsReq;
import com.flowiee.pms.modules.media.entity.FileStorage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionGoodsService {
    //Bases
    Page<TransactionGoodsDTO> getTransactionGoods(TransactionGoodsType pTranType, TransactionGoodsReq pReq);

    TransactionGoods findEntById(Long pId, boolean pThrowException);

    TransactionGoodsDTO findDtoById(Long pId, boolean pThrowException);

    //Import transactions
    TransactionGoodsDTO createImportTransaction(TransactionGoodsDTO pTransactionGoodsDto) throws Exception;

    TransactionGoodsDTO updateImportTransaction(TransactionGoodsDTO pTransactionGoodsDto, Long pTranId);

    String deleteImportTransaction(Long pTranId);

    void restockReturnedItems(Long pStorageId, String pOrderCode);

    //Export transactions
    TransactionGoodsDTO createExportTransaction(TransactionGoodsDTO pTransactionGoodsDto);

    TransactionGoodsDTO updateExportTransaction(TransactionGoodsDTO pTransactionGoodsDto, Long pTranId);

    String generateItemsByOrderCode(Long pTranId, String pOrderCode);

    String deleteExportTransaction(Long pTranId);

    //Joins
    List<TransactionGoodsItemDTO> getItems(Long pTranId);

    List<TransactionGoodsItem> addItems(Long pTranId, TransactionGoodsReq pReq);

    List<FileStorage> getUploadedImages(Long pTranId);
}