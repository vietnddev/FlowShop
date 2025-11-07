package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.entity.AccountRole;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.security.UserPrincipal;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.modules.staff.service.RoleService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final RoleService mvRoleService;
	private final AccountRepository mvAccountRepository;

    public UserDetailsServiceImpl(@Lazy RoleService mvRoleService, @Lazy AccountRepository mvAccountRepository) {
        this.mvRoleService = mvRoleService;
        this.mvAccountRepository = mvAccountRepository;
    }

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account lvAccount = mvAccountRepository.findByUsername(username);
		if (lvAccount == null) {
			throw new UsernameNotFoundException(username);
		}
		return initUserPrincipal(lvAccount);
	}

	private UserPrincipal initUserPrincipal(Account pAccount) {
		if (pAccount.isLocked()) {
			throw new AccountLockedException();
		}

		if (pAccount.isPasswordExpired()) {
			throw new AppException("Password has expired for operator " + pAccount.getUsername());
		}

		UserPrincipal lvUserPrincipal = new UserPrincipal();
		lvUserPrincipal.setId(pAccount.getId());
		lvUserPrincipal.setEmail(pAccount.getEmail());
		lvUserPrincipal.setUsername(pAccount.getUsername());
		lvUserPrincipal.setPassword(pAccount.getPassword());
		lvUserPrincipal.setBranchId(pAccount.getBranch() != null ? pAccount.getBranch().getId() : null);
		lvUserPrincipal.setAccountNonExpired(true);
		lvUserPrincipal.setAccountNonLocked(true);
		lvUserPrincipal.setCredentialsNonExpired(true);
		lvUserPrincipal.setEnabled(AccountStatus.N.name().equals(pAccount.getStatus()));
		lvUserPrincipal.setEntity(pAccount);

		Set<GrantedAuthority> lvGrantedAuthorities = new HashSet<>();
		lvGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + pAccount.getRole()));

		for (AccountRole lvRight : mvRoleService.findByAccountId(pAccount.getId())) {
			lvGrantedAuthorities.add(new SimpleGrantedAuthority(lvRight.getAction()));
		}

		if (pAccount.getGroupAccount() != null) {
			for (AccountRole lvRight : mvRoleService.findByGroupId(pAccount.getGroupAccount().getId())) {
				lvGrantedAuthorities.add(new SimpleGrantedAuthority(lvRight.getAction()));
			}
		}

		lvUserPrincipal.setAuthorities(lvGrantedAuthorities);

		return lvUserPrincipal;
	}
}