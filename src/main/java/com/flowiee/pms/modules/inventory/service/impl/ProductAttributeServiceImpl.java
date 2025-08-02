package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductAttribute;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.modules.inventory.dto.ProductAttributeDTO;
import com.flowiee.pms.modules.inventory.repository.ProductAttributeRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.modules.inventory.service.ProductAttributeService;
import com.flowiee.pms.modules.inventory.service.ProductHistoryService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class ProductAttributeServiceImpl extends BaseService<ProductAttribute, ProductAttributeDTO, ProductAttributeRepository> implements ProductAttributeService {
    private final ProductHistoryService mvProductHistoryService;
    private final SystemLogService mvSystemLogService;

    public ProductAttributeServiceImpl(ProductAttributeRepository pEntityRepository, ProductHistoryService pProductHistoryService, SystemLogService pSystemLogService) {
        super(ProductAttribute.class, ProductAttributeDTO.class, pEntityRepository);
        this.mvProductHistoryService = pProductHistoryService;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<ProductAttributeDTO>find(BaseParameter pParam) {
        return this.findAll(-1, -1, null).getContent();
    }

    @Override
    public Page<ProductAttributeDTO> findAll(int pageSize, int pageNum, Long pProductDetailId) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("sort"));
        Page<ProductAttribute> productAttributePage = mvEntityRepository.findByProductId(pProductDetailId, pageable);
        return new PageImpl<>(convertDTOs(productAttributePage.getContent()), pageable, productAttributePage.getTotalElements());
    }

    @Override
    public ProductAttributeDTO findById(Long pAttributeId, boolean pThrowException) {
        return super.findDtoById(pAttributeId, pThrowException);
    }

    @Override
    public ProductAttributeDTO save(ProductAttributeDTO pProductAttribute) {
        ProductAttribute lvAttribute = new ProductAttribute();
        lvAttribute.setProduct(new Product(pProductAttribute.getProductId()));
        lvAttribute.setAttributeName(pProductAttribute.getAttributeName());
        lvAttribute.setAttributeValue(pProductAttribute.getAttributeValue());
        lvAttribute.setSort(pProductAttribute.getSort());
        lvAttribute.setStatus(pProductAttribute.getStatus());

        ProductAttribute lvAttributeCreated = mvEntityRepository.save(lvAttribute);

        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductAttribute, "Thêm mới thuộc tính sản phẩm", lvAttributeCreated.getAttributeName());

        return convertDTO(lvAttribute);
    }

    @Override
    public List<ProductAttributeDTO> saveAll(List<ProductAttributeDTO> pAttributes) {
        if (CollectionUtils.isEmpty(pAttributes)) {
            return Collections.emptyList();
        }

        // Convert DTO → Entity
        List<ProductAttribute> entities = pAttributes.stream()
                .map(dto -> {
                    ProductAttribute attr = new ProductAttribute();
                    attr.setProduct(new Product(dto.getProductId()));
                    attr.setAttributeName(dto.getAttributeName());
                    attr.setAttributeValue(dto.getAttributeValue());
                    attr.setSort(dto.getSort());
                    attr.setStatus(dto.getStatus());
                    return attr;
                })
                .toList();

        List<ProductAttribute> savedEntities = mvEntityRepository.saveAll(entities);

        savedEntities.forEach(attr ->
                mvSystemLogService.writeLogCreate(
                        MODULE.PRODUCT,
                        ACTION.PRO_PRD_U,
                        MasterObject.ProductAttribute,
                        "Thêm mới thuộc tính sản phẩm",
                        attr.getAttributeName()
                )
        );

        return convertDTOs(savedEntities);
    }

    @Override
    public ProductAttributeDTO update(ProductAttributeDTO pAttribute, Long attributeId) {
        ProductAttribute attribute = super.findEntById(attributeId, true);
        //enhance later
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(attribute));

        attribute.setAttributeName(pAttribute.getAttributeName());
        attribute.setAttributeValue(pAttribute.getAttributeValue());
        attribute.setSort(pAttribute.getSort());
        attribute.setStatus(pAttribute.getStatus());
        ProductAttribute lvAttributeUpdated = mvEntityRepository.save(attribute);

        changeLog.setNewObject(lvAttributeUpdated);
        changeLog.doAudit();

        String logTitle = "Cập nhật thuộc tính sản phẩm";

        mvProductHistoryService.save(changeLog.getLogChanges(), logTitle, lvAttributeUpdated.getProduct().getId(), lvAttributeUpdated.getProduct().getId(), lvAttributeUpdated.getId());
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