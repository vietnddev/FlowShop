package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.service.system.MailMediaService;
import com.flowiee.pms.service.system.SendCustomerNotificationService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.enumeration.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SendCustomerNotificationServiceImpl extends BaseService implements SendCustomerNotificationService {
    private final MailMediaService mvMailMediaService;

    @Override
    public void notifyOrderConfirmation(Order pOrderInfo, String pRecipient) {
        if (CoreUtils.isNullStr(pRecipient))
            return;

        Map<String, Object> lvNotificationParameter = new HashMap<>();
        lvNotificationParameter.put(NotificationType.SendNotifyCustomerOnOrderConfirmation.name(), pRecipient);
        lvNotificationParameter.put("customerName", pOrderInfo.getCustomer().getCustomerName());
        lvNotificationParameter.put("orderTime", pOrderInfo.getOrderTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")));
        lvNotificationParameter.put("deliveryAddress", pOrderInfo.getReceiverAddress());
        lvNotificationParameter.put("deliveryPhoneNumber", pOrderInfo.getReceiverPhone());
        lvNotificationParameter.put("deliveryEmail", pOrderInfo.getReceiverEmail());
        lvNotificationParameter.put("totalOrderValue", OrderUtils.calTotalAmount(pOrderInfo.getListOrderDetail(), pOrderInfo.getAmountDiscount()));

        StringBuilder lvRowsBuilder = new StringBuilder("");
        StringBuilder lvRowBuilder = new StringBuilder();
        int i = 1;
        for (OrderDetail lvItem : pOrderInfo.getListOrderDetail()) {
            ProductDetail lvProductDetail = lvItem.getProductDetail();
            BigDecimal lvUnitPrice = lvItem.getPrice();
            int lvQuantity = lvItem.getQuantity();
            lvRowBuilder.append("<tr>");
            lvRowBuilder.append("<td>").append(i++).append("</td>");
            lvRowBuilder.append("<td>").append(lvProductDetail.getVariantName()).append("</td>");
            lvRowBuilder.append("<td>").append(lvUnitPrice).append("</td>");
            lvRowBuilder.append("<td>").append(lvQuantity).append("</td>");
            lvRowBuilder.append("<td>").append(lvUnitPrice.multiply(new BigDecimal(lvQuantity))).append("</td>");
            lvRowBuilder.append("<td>").append(lvItem.getNote()).append("</td>");
            lvRowBuilder.append("</tr>");
            lvRowsBuilder.append(lvRowBuilder.toString());
            lvRowBuilder.setLength(0);
        }
        lvNotificationParameter.put("orderDetails", lvRowsBuilder.toString());

        mvMailMediaService.send(NotificationType.SendNotifyCustomerOnOrderConfirmation, lvNotificationParameter);
    }
}