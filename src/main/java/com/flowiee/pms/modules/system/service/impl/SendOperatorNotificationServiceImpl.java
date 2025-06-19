package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.enumeration.NotificationType;
import com.flowiee.pms.modules.system.service.MailMediaService;
import com.flowiee.pms.modules.system.service.SendOperatorNotificationService;
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