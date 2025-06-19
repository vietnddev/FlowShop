package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.base.FlwSys;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.common.exception.AppException;

import com.flowiee.pms.modules.system.service.SendMailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SendMailServiceImpl implements SendMailService {
    @Override
    public boolean sendMail(String subject, String to, String body) throws UnsupportedEncodingException, MessagingException {
        return sendMail(subject, to, body, null);
    }

    @Override
    public boolean sendMail(String subject, String to, String body, String attachmentPath) throws UnsupportedEncodingException, MessagingException {
        Assert.notNull(subject, "Subject cannot be null!");
        Assert.notNull(to, "Recipient cannot be null!");
        Assert.notNull(body, "Content cannot be null!");

        Map<ConfigCode, SystemConfig> lvSystemConfig = FlwSys.getSystemConfigs();
        String lvHost = CoreUtils.trim(lvSystemConfig.get(ConfigCode.emailHost).getValue());
        int lvPort = SysConfigUtils.getIntValue(lvSystemConfig.get(ConfigCode.emailPort));
        String lvUsername = CoreUtils.trim(lvSystemConfig.get(ConfigCode.emailUser).getValue());
        String lvPassword = CoreUtils.trim(lvSystemConfig.get(ConfigCode.emailPass).getValue());
        Boolean lvSmtpAuth = Boolean.valueOf(CoreUtils.trim(lvSystemConfig.get(ConfigCode.emailSmtpAuth).getValue()));
        Boolean lvSmtpStarttlsEnable = Boolean.valueOf(CoreUtils.trim(lvSystemConfig.get(ConfigCode.emailSmtpStarttlsEnable).getValue()));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(lvHost);
        mailSender.setPort(lvPort);
        mailSender.setUsername(lvUsername);
        mailSender.setPassword(lvPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", lvSmtpAuth);
        props.put("mail.smtp.starttls.enable", lvSmtpStarttlsEnable);

        // Tạo email
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setSubject(subject);
        messageHelper.setTo(to);
        messageHelper.setText(body, true);

        if (ObjectUtils.isNotEmpty(attachmentPath)) {
            File attachmentFile = new File(attachmentPath);

            if (!attachmentFile.exists()) {
                throw new AppException("File attachment not found!");
            }
            long maxSizeInBytes = 10 * 1024 * 1024; // 10MB in bytes
            if (attachmentFile.length() > maxSizeInBytes) {
                throw new AppException("Attachment file size exceeds 10MB limit!");
            }

            messageHelper.addAttachment(attachmentFile.getName(), attachmentFile);
        }

        // Gửi email
        mailSender.send(mimeMessage);

        return true;
    }
}