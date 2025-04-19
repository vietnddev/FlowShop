package com.flowiee.pms.modules.system.service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface SendMailService {
    boolean sendMail(String subject, String to, String body) throws UnsupportedEncodingException, MessagingException;

    boolean sendMail(String subject, String to, String body, String attachmentPath) throws UnsupportedEncodingException, MessagingException;
}