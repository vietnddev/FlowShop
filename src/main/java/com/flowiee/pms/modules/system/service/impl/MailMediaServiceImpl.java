package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.base.FlwSys;
import com.flowiee.pms.common.config.TemplateSendEmail;
import com.flowiee.pms.modules.system.entity.MailMedia;
import com.flowiee.pms.modules.system.repository.MailMediaRepository;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.SendMailUtils;
import com.flowiee.pms.common.enumeration.NotificationType;
import com.flowiee.pms.modules.system.service.MailMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MailMediaServiceImpl implements MailMediaService {
    private final MailMediaRepository mvMailMediaRepository;

    @Override
    public void send(String pDestination, String pSubject, String pMessage) {
        this.send(pDestination, pSubject, pMessage, MailMedia.P_MEDIUM);
    }

    @Override
    public void send(String pDestination, String pSubject, String pMessage, int pPriority) {
        this.send(pDestination, pSubject, pMessage, "vi", true, MailMedia.P_MEDIUM);
    }

    @Override
    public void send(String pDestination, String pSubject, String pMessage, String pLanguage, boolean pIsHtml, int pPriority) {
        String[] emailDestinationArray = pDestination.split(MailMedia.EMAIL_ADDRESS_SEPARATOR);
        Set<String> lvRecipients = new HashSet<>();
        for (String emailDestination : emailDestinationArray) {
            lvRecipients.add(emailDestination.trim());
        }
        for (String lvRecipient : lvRecipients) {
            mvMailMediaRepository.save(MailMedia.builder()
                    .destination(lvRecipient)
                    .subject(pSubject)
                    .language(pLanguage)
                    .message(pMessage)
                    .isHtml(pIsHtml)
                    .priority(pPriority)
                    .build());
        }
    }

    @Override
    public void send(NotificationType pNotificationType, Map<String, Object> pNotificationParameter) {
        TemplateSendEmail.Template lvTemplate = FlwSys.getEmailTemplateConfigs().get(pNotificationType);
        String lvDestination = pNotificationParameter.get(pNotificationType.name()).toString();
        String lvDefaultSubject = SendMailUtils.replaceTemplateParameter(lvTemplate.getSubject(), pNotificationParameter);
        Object lvCustomSubject = pNotificationParameter.get(MailMedia.SUBJECT);
        String lvContent = SendMailUtils.replaceTemplateParameter(lvTemplate.getTemplateContent(), pNotificationParameter);

        this.send(lvDestination, CoreUtils.isNullStr(lvCustomSubject) ? lvDefaultSubject : lvCustomSubject.toString().trim(), lvContent);
    }
}