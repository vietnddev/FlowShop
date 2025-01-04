package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.base.system.Core;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.SystemConfig;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.system.AccountRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.system.ConfigService;
import com.flowiee.pms.service.system.MailMediaService;
import com.flowiee.pms.service.system.ResetPasswordService;
import com.flowiee.pms.common.utils.PasswordUtils;
import com.flowiee.pms.common.enumeration.ConfigCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl extends BaseService implements ResetPasswordService {
    private final AccountRepository mvAccountRepository;
    private final MailMediaService mvMailMediaService;
    private final ConfigService mvConfigService;

    public static final int DEFAULT_PASSWORD_VALIDITY_PERIOD_DAY = 30;

    @Override
    public String resetPassword(Long pAccountId) {
        Account lvAccount = mvAccountRepository.findById(pAccountId)
                .orElseThrow(() -> new EntityNotFoundException(new Object[] {"account"}, null, null));
        String lvEncodedOldPassword = lvAccount.getPassword();
        String lvRawNewPassword;
        String lvEncodedNewPassword;
        do
        {
            // regenerate password if it is the same!
            lvRawNewPassword = PasswordUtils.generatePassword();
            lvEncodedNewPassword = encodePassword(lvRawNewPassword);
        } while (lvEncodedOldPassword.equals(lvEncodedNewPassword));

        lvAccount.setPassword(lvEncodedNewPassword);
        lvAccount.setPasswordExpireDate(LocalDate.now().plusDays(getPasswordValidityPeriod()));
        lvAccount.setResetTokens(null);
        mvAccountRepository.save(lvAccount);

        return lvEncodedNewPassword;
    }

    @Override
    public String encodePassword(String pRawPassword) {
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        return bCrypt.encode(pRawPassword);
    }

    private int getPasswordValidityPeriod() {
        SystemConfig pwdValidityPeriodCnf = Core.mvSystemConfigList.get(ConfigCode.passwordValidityPeriod);
        if (SysConfigUtils.isValid(pwdValidityPeriodCnf)) {
            return pwdValidityPeriodCnf.getIntValue();
        }
        return DEFAULT_PASSWORD_VALIDITY_PERIOD_DAY;
    }

    @Override
    public void setToken(String email, String resetToken) {
        Account account = mvAccountRepository.findByEmail(email);
        account.setResetTokens(resetToken);
        mvAccountRepository.save(account);
    }

    @Override
    public boolean sendToken(String pEmail, HttpServletRequest pRequest) {
        if (pEmail == null || pEmail.isBlank()) {
            throw new BadRequestException("Invalid email!");
        }
        String resetToken = UUID.randomUUID().toString();
        this.setToken(pEmail, resetToken);
        //URL Like This : http://localhost:8080/reset-password?token=dfjdlkfjsldfdlfkdflkdfjdlk
        String fullURL = pRequest.getRequestURL().toString();
        String resetPwdURL = fullURL.replace(pRequest.getServletPath(), "") + "/reset-password?token=" + resetToken;

        logger.info("Reset password for email " + pEmail + " with token " + resetToken + ", resetPwdURL: " + resetPwdURL);

        String subject = "Password Reset for FLOWIEE account";
        String content = "<p>Hello, </p>" +
                "<p>You have requested to reset your password. </p> " +
                "<p>Please click the link to change your password:</p>" +
                "<p><a href=\"" + resetPwdURL + "\">Change my password</a></p>";
        mvMailMediaService.send(pEmail, subject, content);

        return true;
    }

    @Override
    public boolean resetPasswordWithToken(String pToken, String pNewPassword) {
        Account lvAccount = mvAccountRepository.findByResetTokens(pToken);
        if (lvAccount == null)
            return false;

        SystemConfig lvTokenResetValidityMdl = mvConfigService.getSystemConfig(ConfigCode.tokenResetPasswordValidityPeriod.name());
        if (!SysConfigUtils.isValid(lvTokenResetValidityMdl) || lvAccount.isResetTokenExpired(lvTokenResetValidityMdl.getIntValue())) {
            throw new BadRequestException("Token for reset password has expired!");
        }

        String lvNewPassword = PasswordUtils.encodePassword(pNewPassword);
        lvAccount.setPassword(lvNewPassword);
        lvAccount.setResetTokens(null);
        mvAccountRepository.save(lvAccount);

        return true;
    }
}