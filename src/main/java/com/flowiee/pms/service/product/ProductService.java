package com.flowiee.pms.service.product;

import com.flowiee.pms.model.Filter;
import com.flowiee.pms.common.enumeration.PID;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.model.ProductHeld;
import com.flowiee.pms.model.dto.ProductDTO;
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