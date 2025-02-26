package com.flowiee.pms.base.auth;

import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.exception.AuthenticationException;
import com.flowiee.pms.exception.ForbiddenException;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.system.RoleService;
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
    @Autowired
    private UserSession userSession;

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
            String lvActor = userSession.getUserPrincipal().getUsername();
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
        if (userSession.getUserPrincipal().isAdmin()) {
            return true;
        }
        throw new ForbiddenException("This function is for administrator use only!");
    }
}