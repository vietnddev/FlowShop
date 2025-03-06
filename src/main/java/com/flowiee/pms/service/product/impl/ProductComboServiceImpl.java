package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.ProductCombo;
import com.flowiee.pms.entity.product.ProductComboApply;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.repository.product.ProductComboApplyRepository;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.repository.product.ProductComboRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductComboService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductComboServiceImpl extends BaseService implements ProductComboService {
    ProductVariantService mvProductVariantService;
    ProductComboRepository mvProductComboRepository;
    ProductComboApplyRepository mvProductComboApplyRepository;

    @Override
    public Page<ProductCombo> findAll(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("startDate").ascending());
        Page<ProductCombo> productComboPage = mvProductComboRepository.findAll(pageable);
        for (ProductCombo productCombo : productComboPage.getContent()) {
            productCombo.setAmountDiscount(BigDecimal.ZERO);
            productCombo.setTotalValue(BigDecimal.ZERO);
            productCombo.setQuantity(0);
        }
        setProductIncludes(productComboPage.getContent());
        setProductComboStatus(productComboPage.getContent());
        return productComboPage;
    }

    @Override
    public List<ProductCombo> findAll() {
        return this.findAll(-1, -1).getContent();
    }

    @Override
    public ProductCombo findById(Long comboId, boolean pThrowException) {
        Optional<ProductCombo> productCombo = mvProductComboRepository.findById(comboId);
        if (productCombo.isPresent()) {
            List<ProductCombo> productComboList = List.of(productCombo.get());
            setProductIncludes(productComboList);
            return productComboList.get(0);
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"product combo"}, null, null);
        } else {
            return productCombo.orElse(null);
        }
    }

    @Transactional
    @Override
    public ProductCombo save(ProductCombo productCombo) {
        if (productCombo.getAmountDiscount() == null) {
            productCombo.setAmountDiscount(BigDecimal.ZERO);
        }
        ProductCombo comboSaved = mvProductComboRepository.save(productCombo);
        if (productCombo.getApplicableProducts() != null) {
            for (ProductVariantDTO productVariant : productCombo.getApplicableProducts()) {
                if (productVariant.getId() != null) {
                    mvProductComboApplyRepository.save(ProductComboApply.builder().comboId(comboSaved.getId()).productVariantId(productVariant.getId()).build());
                }
            }
        }
        systemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_CBO_C, MasterObject.ProductCombo, "Thêm mới combo sản phẩm", comboSaved.getComboName());
        return comboSaved;
    }

    @Override
    public ProductCombo update(ProductCombo productCombo, Long comboId) {
        ProductCombo optional = this.findById(comboId, true);

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(optional));

        productCombo.setId(comboId);
        ProductCombo comboUpdated = mvProductComboRepository.save(productCombo);

        changeLog.setNewObject(comboUpdated);
        changeLog.doAudit();

        systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_CBO_C, MasterObject.ProductCombo, "Cập nhật combo sản phẩm", changeLog);

        return comboUpdated;
    }

    @Override
    public String delete(Long comboId) {
        ProductCombo comboBefore = this.findById(comboId, true);
        mvProductComboRepository.deleteById(comboBefore.getId());
        systemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_CBO_C, MasterObject.ProductCombo, "Cập nhật combo sản phẩm", comboBefore.getComboName());
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private void setProductIncludes(List<ProductCombo> productComboPage) {
        for (ProductCombo productCombo : productComboPage) {
            List<ProductVariantDTO> applicableProducts = new ArrayList<>();
            for (ProductComboApply applicableProduct : mvProductComboApplyRepository.findByComboId(productCombo.getId())) {
                ProductVariantDTO productVariantDTO = mvProductVariantService.findById(applicableProduct.getProductVariantId(), false);
                if (productVariantDTO != null) {
                    applicableProducts.add(productVariantDTO);
                }
            }
            productCombo.setApplicableProducts(applicableProducts);
        }
    }

    private void setProductComboStatus(List<ProductCombo> productComboPage) {
        for (ProductCombo productCombo : productComboPage) {
            String status = ProductComboStatus.I.getLabel();
            if (productCombo.getStartDate() != null && productCombo.getEndDate() != null) {
                if ((productCombo.getStartDate().isBefore(LocalDate.now()) || productCombo.getStartDate().isEqual(LocalDate.now())) &&
                        (productCombo.getEndDate().isAfter(LocalDate.now()) || productCombo.getStartDate().isEqual(LocalDate.now()))) {
                    status = ProductComboStatus.A.getLabel();
                }
            }
            productCombo.setStatus(status);
        }
    }
}