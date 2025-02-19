package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.product.GiftCatalog;
import com.flowiee.pms.entity.sales.Customer;
import com.flowiee.pms.entity.sales.GiftRedemption;
import com.flowiee.pms.entity.sales.LoyaltyTransaction;
import com.flowiee.pms.repository.product.GiftCatalogRepository;
import com.flowiee.pms.repository.sales.CustomerRepository;
import com.flowiee.pms.repository.sales.GiftRedemptionRepository;
import com.flowiee.pms.repository.sales.LoyaltyTransactionRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.GiftRedemptionService;
import com.flowiee.pms.common.enumeration.LoyaltyTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GiftRedemptionServiceImpl extends BaseService implements GiftRedemptionService {
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final GiftRedemptionRepository giftRedemptionRepository;
    private final GiftCatalogRepository giftCatalogRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public void redeemGift(Long customerId, Long giftId) {
        // Check gift
        GiftCatalog gift = giftCatalogRepository.findById(giftId)
                .orElseThrow(() -> new RuntimeException("Quà tặng không tồn tại hoặc không khả dụng"));

        if (!gift.getIsActive())
            throw new RuntimeException("Quà tặng không khả dụng");

        if (gift.getStock() != null && gift.getStock() <= 0)
            throw new RuntimeException("Quà tặng đã hết");

        // Check customer info
        Customer customerPoints = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tích điểm của khách hàng"));

        if (customerPoints.getBonusPoints() < gift.getRequiredPoints())
            throw new RuntimeException("Điểm của bạn không đủ để đổi quà");

        // Deduct points and reduce the number of gifts
        customerPoints.setBonusPoints(customerPoints.getBonusPoints() - gift.getRequiredPoints());
        customerRepository.save(customerPoints);

        if (gift.getStock() != null) {
            gift.setStock(gift.getStock() - 1);
            giftCatalogRepository.save(gift);
        }

        // Save history redemption
        GiftRedemption redemption = new GiftRedemption();
        redemption.setCustomer(customerPoints);
        redemption.setGiftCatalog(gift);
        redemption.setRedemptionDate(LocalDateTime.now());
        redemption.setPointsUsed(gift.getRequiredPoints());
        giftRedemptionRepository.save(redemption);

        // Lưu giao dịch LoyaltyTransaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomer(customerPoints);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(LoyaltyTransactionType.REDEEM);
        transaction.setPoints(gift.getRequiredPoints());
        transaction.setRemark("Redeemed gift: " + gift.getName());
        loyaltyTransactionRepository.save(transaction);
    }
}