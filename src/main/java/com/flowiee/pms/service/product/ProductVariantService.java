package com.flowiee.pms.service.product;

import com.flowiee.pms.model.dto.ProductVariantTempDTO;
import com.flowiee.pms.service.BaseCurdService;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductVariantService extends BaseCurdService<ProductVariantDTO> {
    Page<ProductVariantDTO> findAll(int pageSize, int pageNum, Long pProductId, Long pTicketImport, Long pColor, Long pSize, Long pFabricType, Boolean pAvailableForSales);

    boolean isProductVariantExists(long productId, long colorId, long sizeId, long fabricTypeId);

    List<ProductVariantTempDTO> findStorageHistory(Long productVariantId);
}