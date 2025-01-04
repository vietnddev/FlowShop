package com.flowiee.pms.schedule;

import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.system.MailMedia;
import com.flowiee.pms.entity.system.SystemConfig;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.repository.system.AccountRepository;
import com.flowiee.pms.repository.system.ConfigRepository;
import com.flowiee.pms.service.system.MailMediaService;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.enumeration.NotificationType;
import com.flowiee.pms.common.enumeration.ScheduleTask;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotifyProductExpiryScheduleExecutor extends ScheduleExecutor {
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private MailMediaService mailMediaService;

    public NotifyProductExpiryScheduleExecutor() {
        super();
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")//Run at 0h every day
    @Override
    public void init() throws AppException {
        super.init(ScheduleTask.NotifyProductExpiry);
    }

    @Override
    public void doProcesses() throws AppException{
        SystemConfig lvDaySendNotifyBeforeProductExpiry =
                configRepository.findByCode(ConfigCode.daySendNotifyBeforeProductExpiry.name());
        if (!SysConfigUtils.isValid(lvDaySendNotifyBeforeProductExpiry)) {
            return;
        }
        int lvDayBeforeExpiry = lvDaySendNotifyBeforeProductExpiry.getIntValue();

        LocalDate lvExpiredDate = LocalDate.now();
        List<ProductDetail> productExpiredList = productDetailRepository.findByExpiryDate(lvExpiredDate);
        if (ObjectUtils.isEmpty(productExpiredList))
            return;
        sendMail(productExpiredList, lvExpiredDate, true);

        LocalDate lvExpiryDate = lvExpiredDate.plusDays(lvDayBeforeExpiry);
        List<ProductDetail> productNearExpiry = productDetailRepository.findByExpiryDate(lvExpiryDate);
        if (ObjectUtils.isEmpty(productNearExpiry))
            return;
        sendMail(productNearExpiry, lvExpiryDate, false);
    }

    void sendMail(List<ProductDetail> pProductDetailList, LocalDate pExpiryDate, boolean pExpired) {
        NotificationType lvNotifyType = NotificationType.ProductExpirationAlert;
        StringBuilder lvRowsBuilder = new StringBuilder("");
        StringBuilder lvRowBuilder = new StringBuilder();
        int i = 1;
        for (ProductDetail productDetail : pProductDetailList) {
            lvRowBuilder.append("<tr>");
            lvRowBuilder.append("<td>").append(i++).append("</td>");
            lvRowBuilder.append("<td>").append(productDetail.getVariantCode()).append("</td>");
            lvRowBuilder.append("<td>").append(productDetail.getVariantName()).append("</td>");
            lvRowBuilder.append("</tr>");
            lvRowsBuilder.append(lvRowBuilder.toString());
            lvRowBuilder.setLength(0);
        }
        String lvEmailDestination = accountRepository.findByUsername(Constants.ADMINISTRATOR).getEmail();
        String lvExpiryDate = pExpiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String lvShortDescription = pExpired ? String.format("Danh sách sản phẩm đã hết hạn sử dụng từ ngày %s:", lvExpiryDate)
                : String.format("Danh sách sản phẩm sắp hết hạn sử dụng vào ngày %s:", lvExpiryDate);
        String lvSubject = pExpired ? "[FLOWIEE] List of expired products on " + lvExpiryDate
                : "[FLOWIEE] List of products nearing expiration on " + lvExpiryDate;

        Map<String, Object> lvParameter = new HashMap<>();
        lvParameter.put(lvNotifyType.name(), lvEmailDestination);
        lvParameter.put(MailMedia.SUBJECT, lvSubject);
        lvParameter.put("shortDescription", lvShortDescription);
        lvParameter.put(MailMedia.ROWs, lvRowsBuilder.toString());

        mailMediaService.send(lvNotifyType, lvParameter);
    }
}