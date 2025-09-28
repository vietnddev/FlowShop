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
    Page<TransactionGoodsDTO> getTransactionGoods(TransactionGoodsType pTranType, TransactionGoodsReq pReq);

    TransactionGoods findEntById(Long pId, boolean pThrowException);

    TransactionGoodsDTO findDtoById(Long pId, boolean pThrowException);

    //Import transactions
    TransactionGoodsDTO createImportTransaction(TransactionGoodsDTO pTransactionGoodsDto) throws Exception;

    TransactionGoodsDTO updateImportTransaction(TransactionGoodsDTO pTransactionGoodsDto, Long pTranId);

    List<TransactionGoodsItemDTO> getImportItems(Long pTranId);

    List<TransactionGoodsItem> addImportItems(Long pTranId, TransactionGoodsReq pReq);

    String deleteImportTransaction(Long pTranId);

    void restockReturnedItems(Long pStorageId, String pOrderCode);

    List<FileStorage> getUploadedImagesImport(Long pTranId);

    //Export transactions
    TransactionGoodsDTO createExportTransaction(TransactionGoodsDTO pTransactionGoodsDto);

    TransactionGoodsDTO updateExportTransaction(TransactionGoodsDTO pTransactionGoodsDto, Long pTranId);

    TransactionGoodsDTO createExportTransactionFromOrder(TransactionGoodsDTO pTransactionGoodsDto, String pOrderCode);

    String deleteExportTransaction(Long pTranId);
}