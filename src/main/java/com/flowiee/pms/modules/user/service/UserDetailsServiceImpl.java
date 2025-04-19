package com.flowiee.pms.modules.user.service;

import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.PasswordUtils;
import com.flowiee.pms.modules.log.entity.SystemLog;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.user.entity.AccountRole;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.security.UserPrincipal;
import com.flowiee.pms.modules.user.repository.AccountRepository;
import com.flowiee.pms.modules.log.repository.SystemLogRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.system.service.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends BaseService implements UserDetailsService {
	private final RoleService mvRoleService;
	private final AccountRepository mvAccountRepository;
	private final SystemLogRepository mvSystemLogRepository;

	@Value("${system.login.bypass}")
	private boolean mvSystemByPass;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account accountEntity = mvAccountRepository.findByUsername(username);
		UserPrincipal userPrincipal = null;
		if (accountEntity != null) {
			if (accountEntity.isLocked()) {
				throw new AccountLockedException();
			}
			if (accountEntity.isPasswordExpired()) {
				throw new AppException("Password has expired for operator " + accountEntity.getUsername());
			}

			userPrincipal = new UserPrincipal(accountEntity);

			if (mvSystemByPass) {
				userPrincipal.setPassword(PasswordUtils.encodePassword(CommonUtils.defaultNewPassword));
			}

			Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + accountEntity.getRole()));
			for (AccountRole right : mvRoleService.findByAccountId(accountEntity.getId())) {
				grantedAuthorities.add(new SimpleGrantedAuthority(right.getAction()));
			}
			if (accountEntity.getGroupAccount() != null) {
				for (AccountRole right : mvRoleService.findByGroupId(accountEntity.getGroupAccount().getId())) {
					grantedAuthorities.add(new SimpleGrantedAuthority(right.getAction()));
				}
			}
			userPrincipal.setAuthorities(grantedAuthorities);

			WebAuthenticationDetails details = null;
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				Object authDetails = authentication.getDetails();
				if (authDetails instanceof WebAuthenticationDetails) {
					details = (WebAuthenticationDetails) authDetails;
				}
			}
			userPrincipal.setIp(details != null ? details.getRemoteAddress() : "unknown");
			userPrincipal.setCreatedBy(accountEntity.getId());
			userPrincipal.setLastUpdatedBy(accountEntity.getUsername());

			SystemLog systemLog = SystemLog.builder()
					.module(MODULE.SYSTEM.name())
					.function(ACTION.SYS_LOGIN.name())
					.object(MasterObject.Account.name())
					.mode(LogType.LI.name())
					.content(accountEntity.getUsername() + " login")
					.title("Login")
					.ip(userPrincipal.getIp())
					.account(accountEntity)
					.build();
			systemLog.setCreatedBy(accountEntity.getId());

			mvSystemLogRepository.save(systemLog);
		} else {
            logger.error("User not found with username: {}", username);
		}

		return userPrincipal;
	}
}