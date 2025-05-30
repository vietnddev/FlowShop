package com.flowiee.pms.common.security;

import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.exception.AuthenticationException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
public class UserPrincipal extends Account implements UserDetails {
	@Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private Long branchId;
    private String ip;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private Set<GrantedAuthority> grantedAuthorities;
    private Account entity;

    public UserPrincipal(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        if (account.getBranch() != null)
            this.branchId = account.getBranch().getId();
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
        this.setEmail(account.getEmail());
        this.entity = account;
    }

    public UserPrincipal(Long id, String username, String ip) {
        this.id = id;
        this.username = username;
        this.ip = ip;
    }

    public void setAuthorities(Set<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public static UserPrincipal anonymousUser() {
        return new UserPrincipal(0l, "anonymous", "unknown");
    }

    public boolean isAdmin() {
        return Constants.ADMINISTRATOR.equals(this.username);
    }

    public Account getEntity() {
        if (entity == null || entity.getId() == null || entity.getId() == 0) {
            throw new AuthenticationException();
        }
        return entity;
    }
}