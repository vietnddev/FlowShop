package com.flowiee.pms.shared.base;

import com.flowiee.pms.shared.util.SecurityUtils;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.shared.util.SysConfigUtils;
import com.flowiee.pms.shared.exception.AuthenticationException;
import com.flowiee.pms.shared.exception.ForbiddenException;
import com.flowiee.pms.shared.constant.Constants;
import com.flowiee.pms.shared.enums.ACTION;
import com.flowiee.pms.system.service.RoleService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BaseAuthorize {
    @Autowired
    private RoleService roleService;

    @SneakyThrows
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException();
        }
        if ("anonymousUser".equalsIgnoreCase(authentication.getPrincipal().toString())) {
            throw new AuthenticationException();
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ANONYMOUS".equalsIgnoreCase(authority.getAuthority())) {
                throw new AuthenticationException();
            }
        }
        return authentication.isAuthenticated();
    }
    
    protected boolean isAuthorized(ACTION action, boolean throwException) {
        if (isAuthenticated()) {
            String lvActor = SecurityUtils.getCurrentUser().getUsername();
            String lvActionName = action.name();

            if (Constants.ADMINISTRATOR.equals(lvActor)) {
                return true;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals(lvActionName)) {
                    return true;
                }
            }

            if (SysConfigUtils.isYesOption(ConfigCode.forceApplyAccountRightsNoNeedReLogin)) {
                if (roleService.checkTempRights(lvActor, lvActionName)) {
                    return true;
                }
            }

            if (throwException) {
                throw new ForbiddenException("You are not authorized to use this function!");
            } else {
                return false;
            }
        }
        throw new AuthenticationException();
    }

    protected boolean vldAdminRole() {
        if (SecurityUtils.getCurrentUser().isAdmin()) {
            return true;
        }
        throw new ForbiddenException("This function is for administrator use only!");
    }
}