package com.flowiee.pms.product.service;

import com.flowiee.pms.product.enums.ProductStatus;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.model.ProductVariantSearchRequest;
import com.flowiee.pms.product.dto.ProductVariantTempDTO;
import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.product.dto.ProductVariantDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductVariantService extends ICurdService<ProductVariantDTO> {
    Page<ProductVariantDTO> findAll(ProductVariantSearchRequest pRequest);

    List<ProductVariantDTO> getProductsOutOfStock();

    ProductDetail findEntById(Long pVariantId, boolean pThrowException);

    boolean checkVariantExisted(long productId, long colorId, long sizeId, long fabricTypeId);

    List<ProductVariantTempDTO> findStorageHistoryByProductId(Long productId);

    List<ProductVariantTempDTO> findStorageHistoryByVariantId(Long productVariantId);

    void updateLowStockThreshold(Long productId, int threshold);

    void updateStockQuantity(Long pProductVariantId, Integer pQuantity, String pUpdateType);

    void updateDefectiveQuantity(Long pProductVariantId, Integer pQuantity, String pUpdateType);

    void updateStatus(Long pProductVariantId, ProductStatus pStatus);

    List<ProductVariantDTO> save(Long pProductId, List<ProductVariantDTO> pVariantDTOs);
}