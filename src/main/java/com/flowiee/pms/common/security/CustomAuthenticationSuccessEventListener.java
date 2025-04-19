package com.flowiee.pms.common.security;

import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.user.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final AccountRepository accountRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
        Account account = accountRepository.findByUsername(userPrincipal.getUsername());
        if (account != null) {
            account.setFailLogonCount(0);
            accountRepository.save(account);
        }
    }
}