package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.GiftCatalog;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.product.GiftCatalogRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.GiftCatalogService;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiftCatalogServiceImpl extends BaseService implements GiftCatalogService {
    private final GiftCatalogRepository giftCatalogRepository;

    @Override
    public List<GiftCatalog> findAll() {
        return giftCatalogRepository.findAll();
    }

    @Override
    public GiftCatalog findById(Long entityId, boolean pThrowException) {
        Optional<GiftCatalog> entityOptional = giftCatalogRepository.findById(entityId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"gift catalog"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public GiftCatalog save(GiftCatalog entity) {
        return giftCatalogRepository.save(entity);
    }

    @Override
    public GiftCatalog update(GiftCatalog updatedGift, Long id) {
        GiftCatalog existingGift = giftCatalogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quà tặng không tồn tại"));

        existingGift.setName(updatedGift.getName());
        existingGift.setDescription(updatedGift.getDescription());
        existingGift.setRequiredPoints(updatedGift.getRequiredPoints());
        existingGift.setStock(updatedGift.getStock());
        existingGift.setIsActive(updatedGift.getIsActive());

        return giftCatalogRepository.save(existingGift);
    }

    @Override
    public String delete(Long entityId) {
        giftCatalogRepository.deleteById(entityId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<GiftCatalog> getActiveGifts() {
        return giftCatalogRepository.findByIsActiveTrue();
    }
}