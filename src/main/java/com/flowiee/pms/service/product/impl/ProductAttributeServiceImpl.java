package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.ProductAttribute;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.model.dto.ProductAttributeDTO;
import com.flowiee.pms.repository.product.ProductAttributeRepository;
import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.service.product.ProductAttributeService;
import com.flowiee.pms.service.product.ProductHistoryService;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.service.system.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductAttributeServiceImpl extends BaseGService<ProductAttribute, ProductAttributeDTO, ProductAttributeRepository> implements ProductAttributeService {
    private final ProductHistoryService mvProductHistoryService;
    private final SystemLogService mvSystemLogService;

    public ProductAttributeServiceImpl(ProductAttributeRepository pEntityRepository, ProductHistoryService pProductHistoryService, SystemLogService pSystemLogService) {
        super(ProductAttribute.class, ProductAttributeDTO.class, pEntityRepository);
        this.mvProductHistoryService = pProductHistoryService;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<ProductAttributeDTO> findAll() {
        return this.findAll(-1, -1, null).getContent();
    }

    @Override
    public Page<ProductAttributeDTO> findAll(int pageSize, int pageNum, Long pProductDetailId) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("sort"));
        Page<ProductAttribute> productAttributePage = mvEntityRepository.findByProductVariantId(pProductDetailId, pageable);
        return new PageImpl<>(convertDTOs(productAttributePage.getContent()), pageable, productAttributePage.getTotalElements());
    }

    @Override
    public ProductAttributeDTO findById(Long pAttributeId, boolean pThrowException) {
        return super.findById(pAttributeId, pThrowException);
    }

    @Override
    public ProductAttributeDTO save(ProductAttributeDTO pProductAttribute) {
        ProductAttributeDTO productAttributeSaved = super.save(pProductAttribute);
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductAttribute, "Thêm mới thuộc tính sản phẩm", productAttributeSaved.getAttributeName());
        return productAttributeSaved;
    }

    @Override
    public ProductAttributeDTO update(ProductAttributeDTO pAttribute, Long attributeId) {
        ProductAttribute attribute = super.findById(attributeId).orElseThrow(() -> new BadRequestException());
        //enhance later
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(attribute));

        attribute.setAttributeName(pAttribute.getAttributeName());
        attribute.setAttributeValue(pAttribute.getAttributeValue());
        attribute.setSort(pAttribute.getSort());
        attribute.setStatus(pAttribute.isStatus());
        ProductAttribute lvAttributeUpdated = mvEntityRepository.save(attribute);

        changeLog.setNewObject(lvAttributeUpdated);
        changeLog.doAudit();

        String logTitle = "Cập nhật thuộc tính sản phẩm";

        mvProductHistoryService.save(changeLog.getLogChanges(), logTitle, lvAttributeUpdated.getProductDetail().getProduct().getId(), lvAttributeUpdated.getProductDetail().getId(), lvAttributeUpdated.getId());
        mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductAttribute, "Cập nhật thuộc tính sản phẩm", changeLog);

        return convertDTO(lvAttributeUpdated);
    }

    @Override
    public String delete(Long pAttributeId) {
        super.delete(pAttributeId);
        mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductAttribute, "Xóa thuộc tính sản phẩm", "id: " + pAttributeId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}