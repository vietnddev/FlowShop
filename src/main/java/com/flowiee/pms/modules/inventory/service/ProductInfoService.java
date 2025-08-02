package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.ProductAttributeDTO;
import com.flowiee.pms.modules.inventory.entity.ProductDescription;
import com.flowiee.pms.modules.inventory.model.ProductHeld;
import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.model.ProductSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductInfoService extends ICurdService<ProductDTO> {
    Page<ProductDTO> findAll(ProductSearchRequest pRequest, boolean pFullInformation);

    Product findEntById(Long entityId, boolean throwException);

    List<Product> findProductsIdAndProductName();

    boolean productInUse(Long productId);

    List<ProductHeld> getProductHeldInUnfulfilledOrder();

    List<ProductDTO> getDiscontinuedProducts();

    ProductDescription findDescription(Long pProductId);
}