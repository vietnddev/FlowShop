package com.flowiee.pms.modules.schedule;

import com.flowiee.pms.modules.system.entity.MailMedia;
import com.flowiee.pms.modules.system.entity.MailStatus;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.system.repository.MailMediaRepository;
import com.flowiee.pms.modules.system.repository.MailStatusRepository;
import com.flowiee.pms.modules.system.service.SendMailService;
import com.flowiee.pms.common.enumeration.ScheduleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MailNotificationScheduleExecutor extends ScheduleExecutor {
    @Autowired
    private MailStatusRepository mailStatusRepository;
    @Autowired
    private MailMediaRepository mailMediaRepository;
    @Autowired
    private SendMailService sendMailService;

    public MailNotificationScheduleExecutor() {
        super();
    }

    @Scheduled(cron = "*/15 * * * * ?")
    @Override
    public void init() throws AppException {
        super.init(ScheduleTask.MailNotification);
    }

    @Override
    public void doProcesses() throws AppException {
        int emailSentQty = 0;
        List<MailMedia> emailReadyToSendList = mailMediaRepository.getEmailReadyToSend();
        for (MailMedia mailMedia : emailReadyToSendList) {
            String[] emailDestinationArray = mailMedia.getDestination().split(MailMedia.EMAIL_ADDRESS_SEPARATOR);
            String errorMsg = null;
            String sendStatus = "success";
            try {
                Set<String> lvRecipients = new HashSet<>();
                for (String emailDestination : emailDestinationArray) {
                    lvRecipients.add(emailDestination.trim());
                }
                for (String lvRecipient : lvRecipients) {
                    sendMailService.sendMail(mailMedia.getSubject(), lvRecipient, mailMedia.getMessage(), mailMedia.getAttachment());
                    emailSentQty += 1;
                }
            } catch (Throwable ex) {
                logger.error("An error occurred while send an email: " + ex.getMessage(), ex);
                errorMsg = ex.getMessage();
                sendStatus = "error";
            } finally {
                mailStatusRepository.save(MailStatus.builder()
                        .refId(mailMedia.getId())
                        .deliveryTime(errorMsg == null ? LocalDateTime.now() : null)
                        .errorMsg(errorMsg)
                        .status(sendStatus)
                        .build());
            }
        }
        if (emailSentQty > 0) {
            logger.info(emailSentQty + (emailSentQty == 1 ? " email has been sent." : " emails have been sent."));
        }
    }
}