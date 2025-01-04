package com.flowiee.pms.service.storage.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.storage.TransactionGoods;
import com.flowiee.pms.entity.storage.TransactionGoodsItem;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.dto.TransactionGoodsDTO;
import com.flowiee.pms.repository.product.MaterialRepository;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.repository.storage.TransactionGoodsRepository;
import com.flowiee.pms.repository.system.AccountRepository;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.storage.TransactionGoodsService;
import com.flowiee.pms.common.enumeration.TransactionGoodsType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionGoodsServiceImpl extends BaseService implements TransactionGoodsService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionGoodsServiceImpl.class);

    private final TransactionGoodsRepository transactionGoodsRepository;
    private final ProductDetailRepository productVariantRepository;
    private final MaterialRepository materialRepository;
    private final AccountRepository accountRepository;
    private final UserSession userSession;
    private final ModelMapper modelMapper;

    @Override
    public Page<TransactionGoodsDTO> getTransactionGoods(int page, int size, String type, String transactionFromDate, String transactionToDate, String transactionDate, String transactionCode, String orderCode, String itemCode, String createBy, String[] sortColumn, String[] sortType) throws Exception {
        if (TransactionGoodsType.ISSUE.equals(TransactionGoodsType.get(type))) {} else {}
        Page<TransactionGoods> transactionGoodsPage = transactionGoodsRepository.findAll(getPageable(page, size));
        return null;
    }

    @Override
    public TransactionGoodsDTO createTransactionGoods(TransactionGoodsDTO transactionGoodsDto) throws Exception {
        try {
            Account user = accountRepository.findByUsername(userSession.getUserPrincipal().getUsername());
            TransactionGoods transaction = modelMapper.map(transactionGoodsDto, TransactionGoods.class);
            transaction.setId(null);
            transaction.setApprovedTime(LocalDateTime.now());
            transaction.setTransactionTime(LocalDateTime.now());
            List<TransactionGoodsItem> lstItems = new ArrayList<>();
            if (!CollectionUtils.isEmpty(transactionGoodsDto.getItems())) {
                transactionGoodsDto.getItems().forEach(i -> {
                    TransactionGoodsItem transactionItem = modelMapper.map(i, TransactionGoodsItem.class);
                    if (!ObjectUtils.isEmpty(i.getProductVariant())) {
                        Optional<ProductDetail> productVariant = productVariantRepository.findById(i.getProductVariant().getId());
                        productVariant.ifPresent(transactionItem::setProductVariant);
                    }
                    if (!ObjectUtils.isEmpty(i.getMaterial())) {
                        Optional<Material> material = materialRepository.findById(i.getMaterial().getId());
                        material.ifPresent(transactionItem::setMaterial);
                    }

                    transactionItem.setQuantity(i.getQuantity());
                    transactionItem.setTransactionGoods(transaction);
                    lstItems.add(transactionItem);
                });
            }
            transaction.setItems(lstItems);

            TransactionGoods result = switch (TransactionGoodsType.get(transactionGoodsDto.getType())) {
                case RECEIPT -> createTransactionGoodsWithTypeReceipt(transaction);
                case ISSUE -> createTransactionGoodsWithTypeIssue(transaction);
                default -> throw new AppException("Please input transaction type!");
            };

            // Return dto after create transaction successful
            TransactionGoodsDTO rs = modelMapper.map(result, TransactionGoodsDTO.class);
//            if (!ObjectUtils.isEmpty(rs.getOrder()))
//                rs.getOrder().setItems(new ArrayList<>());
            return rs;
        } catch (Exception e) {
            LOG.error("Create transaction goods got [{}]", e.getMessage(), e);
            throw e;
        }
    }

    private TransactionGoods createTransactionGoodsWithTypeReceipt(TransactionGoods transactionGoods) {
        return transactionGoods;
    }

    private TransactionGoods createTransactionGoodsWithTypeIssue(TransactionGoods transactionGoods) {
        return transactionGoods;
    }
}