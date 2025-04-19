package com.flowiee.pms.common.security;

import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.modules.user.repository.AccountRepository;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import com.flowiee.pms.common.enumeration.AccountStatus;
import com.flowiee.pms.common.enumeration.ConfigCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final AccountRepository accountRepository;
    private final ConfigRepository  sysConfigRepository;

    private int mvMaxFailLogon = 5;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            if (account.isLocked()) {
                return;
            }
            account.setFailLogonCount(account.getFailLogonCount() + 1);

            SystemConfig lvFailLogonCountMdl = sysConfigRepository.findByCode(ConfigCode.failLogonCount.name());
            if (SysConfigUtils.isValid(lvFailLogonCountMdl)) {
                mvMaxFailLogon = Integer.parseInt(lvFailLogonCountMdl.getValue());
            }
            if (account.getFailLogonCount() >= mvMaxFailLogon) {
                account.setStatus(AccountStatus.L.name());
            }

            accountRepository.save(account);
        }
    }
}