package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.model.ProductVariantSearchRequest;
import com.flowiee.pms.modules.inventory.dto.ProductVariantTempDTO;
import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductVariantService extends ICurdService<ProductVariantDTO> {
    Page<ProductVariantDTO> findAll(ProductVariantSearchRequest pRequest);

    //Page<ProductVariantDTO> findAll(int pageSize, int pageNum, String pTxtSerch, Long pProductId, Long pTicketImport, Long pBrandId, Long pColor, Long pSize, Long pFabricType, Boolean pAvailableForSales, boolean checkInAnyCart);

    boolean variantExisted(long productId, long colorId, long sizeId, long fabricTypeId);

    List<ProductVariantTempDTO> findStorageHistory(Long productVariantId);

    void updateLowStockThreshold(Long productId, int threshold);

    Page<ProductVariantDTO> getProductsOutOfStock(int pageSize, int pageNum);
}