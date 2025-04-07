package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.entity.product.GiftCatalog;
import com.flowiee.pms.model.dto.GiftCatalogDTO;
import com.flowiee.pms.repository.product.GiftCatalogRepository;
import com.flowiee.pms.service.product.GiftCatalogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftCatalogServiceImpl extends BaseGService<GiftCatalog, GiftCatalogDTO, GiftCatalogRepository> implements GiftCatalogService {
    public GiftCatalogServiceImpl(GiftCatalogRepository pEntityRepository) {
        super(GiftCatalog.class, GiftCatalogDTO.class, pEntityRepository);
    }

    @Override
    public List<GiftCatalogDTO> findAll() {
        return super.findAll();
    }

    @Override
    public GiftCatalogDTO findById(Long pEntityId, boolean pThrowException) {
        return super.findById(pEntityId, pThrowException);
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