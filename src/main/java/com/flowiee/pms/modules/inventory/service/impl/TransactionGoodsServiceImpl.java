package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsItemDTO;
import com.flowiee.pms.modules.inventory.entity.*;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsItemReq;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsReq;
import com.flowiee.pms.modules.inventory.repository.TransactionGoodsItemRepository;
import com.flowiee.pms.modules.inventory.service.StorageService;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.repository.MaterialRepository;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.inventory.repository.TransactionGoodsRepository;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionGoodsServiceImpl extends BaseService<TransactionGoods, TransactionGoodsDTO, TransactionGoodsRepository> implements TransactionGoodsService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final TransactionGoodsItemRepository transactionGoodsItemRepository;
    private final ProductDetailRepository productVariantRepository;
    private final MaterialRepository materialRepository;
    private final AccountRepository accountRepository;
    private final StorageService storageService;

    private static final String PREFIX_TRANSACTION_IMPORT_CODE = "I";
    private static final String PREFIX_TRANSACTION_EXPORT_CODE = "E";

    public TransactionGoodsServiceImpl(TransactionGoodsRepository transactionGoodsRepository,
                                       TransactionGoodsItemRepository transactionGoodsItemRepository,
                                       ProductDetailRepository productVariantRepository,
                                       MaterialRepository materialRepository,
                                       AccountRepository accountRepository,
                                       StorageService storageService) {
        super(TransactionGoods.class, TransactionGoodsDTO.class, transactionGoodsRepository);
        this.transactionGoodsItemRepository = transactionGoodsItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.materialRepository = materialRepository;
        this.accountRepository = accountRepository;
        this.storageService = storageService;
    }

    @Override
    public Page<TransactionGoodsDTO> getTransactionGoods(TransactionGoodsReq pRequest) {
        Pageable lvPageable = getPageable(pRequest.getPageNum(), pRequest.getPageSize(), Sort.by("id").descending());
        Page<TransactionGoods> transactionGoodsPage = mvEntityRepository.findAll(lvPageable);
        List<TransactionGoods> transactionGoodsList = transactionGoodsPage.getContent();
        return new PageImpl<>(TransactionGoodsDTO.toDTOs(transactionGoodsList), lvPageable, transactionGoodsPage.getTotalElements());
    }

    @Override
    public TransactionGoods findEntById(Long pId, boolean pThrowException) {
        return super.findEntById(pId, pThrowException);
    }

    @Override
    public TransactionGoodsDTO findDtoById(Long pId, boolean throwException) {
        return TransactionGoodsDTO.toDto(super.findEntById(pId, throwException));
    }

    @Override
    public TransactionGoodsDTO createImportTransaction(TransactionGoodsDTO pTransactionGoodsDto) throws Exception {
        try {
            Storage lvWarehouse = storageService.findEntById(pTransactionGoodsDto.getWarehouse().getId(), true);
            TransactionGoods lvRecordedTransactionGoods = mvEntityRepository.save(TransactionGoods.builder()
                    .code(generateTransactionGoodsCode(pTransactionGoodsDto.getTransactionType()))
                    .title("Draft")
                    .warehouse(lvWarehouse)
                    .build());

//            List<TransactionGoodsItem> lstItems = new ArrayList<>();
//            if (!CollectionUtils.isEmpty(pTransactionGoodsDto.getItems())) {
//                pTransactionGoodsDto.getItems().forEach(i -> {
//                    TransactionGoodsItem transactionItem = modelMapper.map(i, TransactionGoodsItem.class);
//                    if (!ObjectUtils.isEmpty(i.getProductVariant())) {
//                        Optional<ProductDetail> productVariant = productVariantRepository.findById(i.getProductVariant().getId());
//                        productVariant.ifPresent(transactionItem::setProductVariant);
//                    }
//                    if (!ObjectUtils.isEmpty(i.getMaterial())) {
//                        Optional<Material> material = materialRepository.findById(i.getMaterial().getId());
//                        material.ifPresent(transactionItem::setMaterial);
//                    }
//
//                    transactionItem.setQuantity(i.getQuantity());
//                    transactionItem.setTransactionGoods(transaction);
//                    lstItems.add(transactionItem);
//                });
//            }
//            transaction.setItems(lstItems);
//
//            TransactionGoods result = switch (pTransactionGoodsDto.getTransactionType()) {
//                case IMPORT -> createTransactionGoodsWithTypeReceipt(transaction);
//                case EXPORT -> createTransactionGoodsWithTypeIssue(transaction);
//            };
//
//            // Return dto after create transaction successful
//            TransactionGoodsDTO rs = modelMapper.map(result, TransactionGoodsDTO.class);
////            if (!ObjectUtils.isEmpty(rs.getOrder()))
////                rs.getOrder().setItems(new ArrayList<>());

            return super.convertDTO(lvRecordedTransactionGoods);
        } catch (Exception e) {
            LOG.error("Create transaction goods got [{}]", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransactionGoodsItemDTO> getImportItems(Long pTranId) {
        return TransactionGoodsItemDTO.toDTOs(transactionGoodsItemRepository.findByTranId(pTranId));
    }

    @Override
    public List<TransactionGoodsItem> addImportItems(Long pTranId, TransactionGoodsReq pReq) {
        TransactionGoods lvTransactionGoods = super.findEntById(pTranId, true);

        List<TransactionGoodsItemReq> lvItems = pReq.getReqItems();
        if (ObjectUtils.isEmpty(lvItems)) {
            throw new BadRequestException("Please choose at least one product!");
        }

        List<TransactionGoodsItem> lvSavedItems = new ArrayList<>();
        for (TransactionGoodsItemReq lvItem : lvItems) {
            ProductDetail lvProductVariant = productVariantRepository.findById(lvItem.getProductVariantId())
                    .orElseThrow(() -> new BadRequestException(String.format("ItemId %s is invalid!", lvItem.getProductVariantId())));
            int lvItemQty = CoreUtils.coalesce(lvItem.getQuantity(), 1);

            TransactionGoodsItem lvTransactionGoodsItem = transactionGoodsItemRepository.findByTranIdAndProductVariantId(lvTransactionGoods.getId(), lvProductVariant.getId());
            if (lvTransactionGoodsItem != null) {
                lvTransactionGoodsItem.setQuantity(lvTransactionGoodsItem.getQuantity() + lvItemQty);
                transactionGoodsItemRepository.save(lvTransactionGoodsItem);
            } else {
                lvSavedItems.add(transactionGoodsItemRepository.save(TransactionGoodsItem.builder()
                        .transactionGoods(lvTransactionGoods)
                        .productVariant(lvProductVariant)
                        .quantity(lvItemQty)
                        .build()));
            }
        }

        return lvSavedItems;
    }

    private String generateTransactionGoodsCode(TransactionGoodsType pType) {
        TransactionGoods lvLatestTrans = mvEntityRepository.findLatestByType(pType);
        String lvLatestCode = lvLatestTrans == null ? "" : CoreUtils.trim(lvLatestTrans.getCode());
        String lvPrefixCode = pType.equals(TransactionGoodsType.IMPORT)
                ? PREFIX_TRANSACTION_IMPORT_CODE
                : PREFIX_TRANSACTION_EXPORT_CODE;
        if (CoreUtils.isNullStr(lvLatestCode)) {
            return lvPrefixCode + "00001";
        }
        int lvCurrentIndex = Integer.parseInt(lvLatestCode.substring(lvPrefixCode.length()));
        return lvPrefixCode + (lvCurrentIndex + 1);
    }
}