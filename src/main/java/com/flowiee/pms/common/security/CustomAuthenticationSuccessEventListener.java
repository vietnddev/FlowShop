package com.flowiee.pms.common.security;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.LogType;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.modules.system.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final AccountRepository mvAccountRepository;
    private final SystemLogRepository mvSystemLogRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
        Account account = mvAccountRepository.findByUsername(userPrincipal.getUsername());
        if (account != null) {
            if (account.getFailLogonCount() > 0) {
                account.setFailLogonCount(0);
                mvAccountRepository.save(account);
            }
            SystemLog lvSystemLog = SystemLog.builder()
                    .module(MODULE.SYSTEM.name())
                    .function(ACTION.SYS_LOGIN.name())
                    .object(MasterObject.Account.name())
                    .mode(LogType.LI.name())
                    .content(account.getUsername() + " login")
                    .title("Login")
                    .ip(userPrincipal.getIp())
                    .account(account)
                    .build();
            lvSystemLog.setCreatedBy(account.getId());
            mvSystemLogRepository.save(lvSystemLog);
        }
    }
}