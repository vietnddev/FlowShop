package com.flowiee.pms.modules.system.service;

import javax.servlet.http.HttpServletRequest;

public interface ResetPasswordService {
    String resetPassword(Long pAccountId);

    boolean resetPasswordWithToken(String token, String newPassword);

    String encodePassword(String pRawPassword);

    void setToken(String email, String resetToken);

    boolean sendToken(String email, HttpServletRequest request);
}