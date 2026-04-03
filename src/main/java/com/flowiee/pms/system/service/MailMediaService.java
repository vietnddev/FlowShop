package com.flowiee.pms.system.service;

import com.flowiee.pms.system.enums.NotificationType;

import java.util.Map;

public interface MailMediaService {
    void send(String pDestination, String pSubject, String pMessage);

    void send(String pDestination, String pSubject, String pMessage, int pPriority);

    void send(String pDestination, String pSubject, String pMessage, String pLanguage, boolean pIsHtml, int pPriority);

    void send(NotificationType pNotificationType, Map<String, Object> pNotificationParameter);
}