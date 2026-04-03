package com.flowiee.pms.promotion.service.impl;

import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.request.BaseParameter;
import com.flowiee.pms.promotion.dto.GiftCatalog;
import com.flowiee.pms.promotion.dto.GiftCatalogDTO;
import com.flowiee.pms.promotion.repository.GiftCatalogRepository;
import com.flowiee.pms.promotion.service.GiftCatalogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftCatalogServiceImpl extends BaseService<GiftCatalog, GiftCatalogDTO, GiftCatalogRepository> implements GiftCatalogService {
    public GiftCatalogServiceImpl(GiftCatalogRepository pEntityRepository) {
        super(GiftCatalog.class, GiftCatalogDTO.class, pEntityRepository);
    }

    @Override
    public List<GiftCatalogDTO>find(BaseParameter pParam) {
        return super.find(pParam);
    }

    @Override
    public GiftCatalogDTO findById(Long pEntityId, boolean pThrowException) {
        return super.findDtoById(pEntityId, pThrowException);
    }

    @Override
    public GiftCatalogDTO save(GiftCatalogDTO pEntity) {
        return super.save(pEntity);
    }

    @Override
    public GiftCatalogDTO update(GiftCatalogDTO pGift, Long pId) {
        GiftCatalog existingGift = super.findById(pId).orElseThrow(() -> new RuntimeException("Quà tặng không tồn tại"));

        existingGift.setName(pGift.getName());
        existingGift.setDescription(pGift.getDescription());
        existingGift.setRequiredPoints(pGift.getRequiredPoints());
        existingGift.setStock(pGift.getStock());
        existingGift.setIsActive(pGift.getIsActive());

        return super.convertDTO(mvEntityRepository.save(existingGift));
    }

    @Override
    public String delete(Long pEntityId) {
        return super.delete(pEntityId);
    }

    @Override
    public List<GiftCatalogDTO> getActiveGifts() {
        return convertDTOs(mvEntityRepository.findByIsActiveTrue());
    }
}