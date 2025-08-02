package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.inventory.entity.Material;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.repository.MaterialRepository;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.inventory.repository.TransactionGoodsRepository;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.common.enumeration.TransactionGoodsType;
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
public class TransactionGoodsServiceImpl extends BaseService<TransactionGoods, TransactionGoodsDTO, TransactionGoodsRepository> implements TransactionGoodsService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final ProductDetailRepository productVariantRepository;
    private final MaterialRepository materialRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public TransactionGoodsServiceImpl(TransactionGoodsRepository transactionGoodsRepository,
                                       ProductDetailRepository productVariantRepository,
                                       MaterialRepository materialRepository,
                                       AccountRepository accountRepository,
                                       UserSession userSession, ModelMapper modelMapper) {
        super(TransactionGoods.class, TransactionGoodsDTO.class, transactionGoodsRepository);
        this.productVariantRepository = productVariantRepository;
        this.materialRepository = materialRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<TransactionGoodsDTO> getTransactionGoods(int page, int size, String type, String transactionFromDate, String transactionToDate, String transactionDate, String transactionCode, String orderCode, String itemCode, String createBy, String[] sortColumn, String[] sortType) throws Exception {
        if (TransactionGoodsType.ISSUE.equals(TransactionGoodsType.get(type))) {} else {}
        Page<TransactionGoods> transactionGoodsPage = mvEntityRepository.findAll(getPageable(page, size));
        return null;
    }

    @Override
    public TransactionGoodsDTO createTransactionGoods(TransactionGoodsDTO transactionGoodsDto) throws Exception {
        try {
            Account user = accountRepository.findByUsername(getUserPrincipal().getUsername());
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