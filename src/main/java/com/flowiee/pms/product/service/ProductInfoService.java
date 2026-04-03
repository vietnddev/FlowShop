package com.flowiee.pms.product.service;

import com.flowiee.pms.product.entity.ProductDescription;
import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.product.dto.ProductDTO;
import com.flowiee.pms.product.entity.Product;
import com.flowiee.pms.product.model.ProductSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductInfoService extends ICurdService<ProductDTO> {
    Page<ProductDTO> findAll(ProductSearchRequest pRequest, boolean pFullInformation);

    Product findEntById(Long entityId, boolean throwException);

    List<Product> findProductsIdAndProductName();

    ProductDescription findDescription(Long pProductId);

    String updateDescription(Long pProductId, String pDescription);
}