package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductAttribute;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductHistory;
import com.flowiee.pms.modules.inventory.dto.ProductHistoryDTO;
import com.flowiee.pms.modules.inventory.repository.ProductHistoryRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.inventory.service.ProductHistoryService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductHistoryServiceImpl extends BaseService<ProductHistory, ProductHistoryDTO, ProductHistoryRepository> implements ProductHistoryService {

    public ProductHistoryServiceImpl(ProductHistoryRepository pProductHistoryRepository) {
        super(ProductHistory.class, ProductHistoryDTO.class, pProductHistoryRepository);
    }

    @Override
    public List<ProductHistoryDTO>find(BaseParameter pParam) {
        return super.convertDTOs(mvEntityRepository.findAll());
    }

    @Override
    public ProductHistoryDTO findById(Long productHistoryId, boolean pThrowException) {
        return super.findDtoById(productHistoryId, pThrowException);
    }

    @Override
    public ProductHistoryDTO save(ProductHistoryDTO productHistory) {
        return super.save(productHistory);
    }

    @Override
    public ProductHistoryDTO update(ProductHistoryDTO pProductHistory, Long pProductHistoryId) {
        return super.update(pProductHistory, pProductHistoryId);
    }

    @Override
    public String delete(Long productHistoryId) {
        return super.delete(productHistoryId);
    }

    @Override
    public List<ProductHistory> findByProduct(Long productId) {
        return mvEntityRepository.findByProductId(productId);
    }

    @Override
    public List<ProductHistory> findPriceChange(Long productDetailId) {
        List<ProductHistory> prices =  mvEntityRepository.findHistoryChangeOfProductDetail(productDetailId, "PRICE");
        for (ProductHistory priceChange : prices) {
            if (priceChange.getProduct() != null) {
                priceChange.setProductId(priceChange.getProduct().getId());
                if (priceChange.getProductDetail() != null) {
                    priceChange.setProductVariantId(priceChange.getProductDetail().getId());
                }
            }
        }
        return prices;
    }

    @Override
    public List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId) {
        List<ProductHistory> logSaved = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : logChanges.entrySet()) {
            String field = entry.getKey();
            String oldValue = entry.getValue()[0] != null ? entry.getValue()[0].toString() : "-";
            String newValue = entry.getValue()[1] != null ? entry.getValue()[1].toString() : "-";
            ProductHistory productHistory = ProductHistory.builder()
                    .title(title)
                    .product(productBaseId != null ? new Product(productBaseId) : null)
                    .productDetail(productVariantId != null ? new ProductDetail(productVariantId) : null)
                    .productAttribute(productAttributeId != null ? new ProductAttribute(productAttributeId) : null)
                    .field(field)
                    .oldValue("null".equals(oldValue) || ObjectUtils.isEmpty(oldValue) ? "-" : oldValue)
                    .newValue("null".equals(newValue) || ObjectUtils.isEmpty(newValue) ? "-" : newValue)
                    .build();
            logSaved.add(mvEntityRepository.save(productHistory));
        }
        return logSaved;
    }
}