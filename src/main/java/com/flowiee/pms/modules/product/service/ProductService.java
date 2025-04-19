package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.model.Filter;
import com.flowiee.pms.common.enumeration.PID;
import com.flowiee.pms.modules.product.entity.Product;
import com.flowiee.pms.modules.product.model.ProductHeld;
import com.flowiee.pms.modules.product.dto.ProductDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<ProductDTO> search(List<Filter> filter);

    Page<ProductDTO> findAll(PID pPID, int pageSize, int pageNum, String pTxtSearch,
                             Long pBrand, Long pProductType, Long pColor, Long pSize, Long pUnit,
                             String pGender, Boolean pIsSaleOff, Boolean pIsHotTrend, String pStatus);

    List<Product> findProductsIdAndProductName();

    ProductDTO saveProduct(ProductDTO dto);

    ProductDTO updateProduct(ProductDTO dto, Long pId);

    String deleteProduct(Long pId);

    boolean productInUse(Long productId);

    List<ProductHeld> getProductHeldInUnfulfilledOrder();

    List<ProductDTO> getDiscontinuedProducts();
}