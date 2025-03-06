package com.flowiee.pms.service.product;

import com.flowiee.pms.model.ProductVariantParameter;
import com.flowiee.pms.model.dto.ProductVariantTempDTO;
import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductVariantService extends BaseCurdService<ProductVariantDTO> {
    Page<ProductVariantDTO> findAll(ProductVariantParameter pParameter);

    Page<ProductVariantDTO> findAll(int pageSize, int pageNum, String pTxtSerch, Long pProductId, Long pTicketImport, Long pBrandId, Long pColor, Long pSize, Long pFabricType, Boolean pAvailableForSales, boolean checkInAnyCart);

    boolean isProductVariantExists(long productId, long colorId, long sizeId, long fabricTypeId);

    List<ProductVariantTempDTO> findStorageHistory(Long productVariantId);

    void updateLowStockThreshold(Long productId, int threshold);

    Page<ProductVariantDTO> getProductsOutOfStock(int pageSize, int pageNum);
}