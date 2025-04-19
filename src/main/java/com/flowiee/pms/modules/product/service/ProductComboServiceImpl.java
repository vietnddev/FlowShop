package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.product.entity.ProductCombo;
import com.flowiee.pms.modules.product.entity.ProductComboApply;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.product.dto.ProductComboDTO;
import com.flowiee.pms.modules.product.dto.ProductVariantDTO;
import com.flowiee.pms.modules.product.repository.ProductComboApplyRepository;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.product.repository.ProductComboRepository;
import com.flowiee.pms.modules.log.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class ProductComboServiceImpl extends BaseGService<ProductCombo, ProductComboDTO, ProductComboRepository> implements ProductComboService {
    private final ProductComboApplyRepository mvProductComboApplyRepository;
    private final ProductVariantService mvProductVariantService;
    private final SystemLogService mvSystemLogService;

    public ProductComboServiceImpl(ProductComboRepository pEntityRepository, ProductVariantService pProductVariantService, ProductComboApplyRepository pProductComboApplyRepository, SystemLogService pSystemLogService) {
        super(ProductCombo.class, ProductComboDTO.class, pEntityRepository);
        this.mvProductVariantService = pProductVariantService;
        this.mvProductComboApplyRepository = pProductComboApplyRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public Page<ProductComboDTO> findAll(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("startDate").ascending());
        Page<ProductCombo> productComboPage = mvEntityRepository.findAll(pageable);
        for (ProductCombo productCombo : productComboPage.getContent()) {
            productCombo.setAmountDiscount(BigDecimal.ZERO);
            productCombo.setTotalValue(BigDecimal.ZERO);
            productCombo.setQuantity(0);
        }
        setProductIncludes(productComboPage.getContent());
        setProductComboStatus(productComboPage.getContent());

        return new PageImpl<>(convertDTOs(productComboPage.getContent()), pageable, productComboPage.getTotalElements());
    }

    @Override
    public List<ProductComboDTO> findAll() {
        return this.findAll(-1, -1).getContent();
    }

    @Override
    public ProductComboDTO findById(Long comboId, boolean pThrowException) {
        Optional<ProductCombo> productCombo = super.findById(comboId);
        if (productCombo.isPresent()) {
            List<ProductCombo> productComboList = List.of(productCombo.get());
            setProductIncludes(productComboList);
            return convertDTO(productComboList.get(0));
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"product combo"}, null, null);
        } else {
            return convertDTO(productCombo.orElse(null));
        }
    }

    @Transactional
    @Override
    public ProductComboDTO save(ProductComboDTO pProductCombo) {
        if (pProductCombo.getAmountDiscount() == null) {
            pProductCombo.setAmountDiscount(BigDecimal.ZERO);
        }
        ProductComboDTO comboSaved = super.save(pProductCombo);
        if (pProductCombo.getApplicableProducts() != null) {
            for (ProductVariantDTO productVariant : pProductCombo.getApplicableProducts()) {
                if (productVariant.getId() != null) {
                    mvProductComboApplyRepository.save(ProductComboApply.builder().comboId(comboSaved.getId()).productVariantId(productVariant.getId()).build());
                }
            }
        }
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_CBO_C, MasterObject.ProductCombo, "Thêm mới combo sản phẩm", comboSaved.getComboName());
        return comboSaved;
    }

    @Override
    public ProductComboDTO update(ProductComboDTO pProductCombo, Long comboId) {
        ProductCombo lvCombo = super.findById(comboId).orElseThrow(() -> new BadRequestException());

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvCombo));

        lvCombo.setId(comboId);
        ProductCombo comboUpdated = mvEntityRepository.save(lvCombo);

        changeLog.setNewObject(comboUpdated);
        changeLog.doAudit();

        mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_CBO_C, MasterObject.ProductCombo, "Cập nhật combo sản phẩm", changeLog);

        return convertDTO(comboUpdated);
    }

    @Override
    public String delete(Long pComboId) {
        super.delete(pComboId);
        mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_CBO_C, MasterObject.ProductCombo, "Cập nhật combo sản phẩm", "id: " + pComboId);
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