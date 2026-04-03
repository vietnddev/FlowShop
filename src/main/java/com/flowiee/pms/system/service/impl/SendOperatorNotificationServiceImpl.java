package com.flowiee.pms.system.service.impl;

import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.system.entity.Account;
import com.flowiee.pms.system.repository.AccountRepository;
import com.flowiee.pms.shared.constant.Constants;
import com.flowiee.pms.system.enums.NotificationType;
import com.flowiee.pms.system.service.MailMediaService;
import com.flowiee.pms.system.service.SendOperatorNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SendOperatorNotificationServiceImpl implements SendOperatorNotificationService {
    private final AccountRepository mvAccountRepository;
    private final MailMediaService mvMailMediaService;

    @Override
    public void notifyWarningLowStock(ProductDetail pProductDetail) {
        Account lvAdmin = mvAccountRepository.findByUsername(Constants.ADMINISTRATOR);
        if (lvAdmin == null)
            return;

        String lvDestination = lvAdmin.getEmail();
        if (lvDestination == null || lvDestination.isBlank())
            return;

        Map<String, Object> lvNotificationParameter = new HashMap<>();
        lvNotificationParameter.put(NotificationType.LowStockAlert.name(), lvDestination);
        lvNotificationParameter.put("productName", pProductDetail.getVariantName());
        lvNotificationParameter.put("currentQuantity", pProductDetail.getStorageQty());
        lvNotificationParameter.put("threshold", pProductDetail.getLowStockThreshold());

        mvMailMediaService.send(NotificationType.LowStockAlert, lvNotificationParameter);
    }
}