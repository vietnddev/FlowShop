package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.product.entity.ProductPrice;
import com.flowiee.pms.modules.product.dto.ProductPriceDTO;
import com.flowiee.pms.modules.product.repository.ProductPriceRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductPriceServiceImpl extends BaseGService<ProductPrice, ProductPriceDTO, ProductPriceRepository> implements ProductPriceService {
    public ProductPriceServiceImpl(ProductPriceRepository pEntityRepository) {
        super(ProductPrice.class, ProductPriceDTO.class, pEntityRepository);
    }

    @Transactional
    @Override
    public String updateProductPrice(Long variantId, BigDecimal pOriginalPrice, BigDecimal pDiscountPrice) {
        return "";
    }

    @Override
    public List<ProductPriceDTO> findPresentPrices(List<Long> pProductVariantIds) {
        if (CollectionUtils.isEmpty(pProductVariantIds)) {
            return List.of();
        }

        List<ProductPrice> lvPriceList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < pProductVariantIds.size(); i += batchSize) {
            List<Long> batch = pProductVariantIds.subList(i, Math.min(i + batchSize, pProductVariantIds.size()));
            lvPriceList.addAll(mvEntityRepository.findPresentPrices(batch));
        }

        return convertDTOs(lvPriceList);
    }
}