package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.model.ProductVariantSearchRequest;
import com.flowiee.pms.modules.inventory.dto.ProductVariantTempDTO;
import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductVariantService extends ICurdService<ProductVariantDTO> {
    Page<ProductVariantDTO> findAll(ProductVariantSearchRequest pRequest);

    Page<ProductVariantDTO> getProductsOutOfStock(int pageSize, int pageNum);

    boolean checkVariantExisted(long productId, long colorId, long sizeId, long fabricTypeId);

    List<ProductVariantTempDTO> findStorageHistory(Long productVariantId);

    void updateLowStockThreshold(Long productId, int threshold);

    void updateStockQuantity(Long pProductVariantId, Integer pQuantity, String pUpdateType);

    void updateDefectiveQuantity(Long pProductVariantId, Integer pQuantity, String pUpdateType);

    ProductDetail findEntById(Long pVariantId, boolean pThrowException);
}