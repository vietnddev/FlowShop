package com.flowiee.pms.security;

import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.common.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@RequestScope
@Component
@Getter
@Setter
public class UserSession {
    @Autowired
    private HttpServletRequest request;

    private UserPrincipal userPrincipal;
    private String token;

    @PostConstruct
    void init() throws Exception {
        try {
            this.reset();
//            this.token = this.request.getHeader(HttpHeaders.AUTHORIZATION);
//            Claims claims = Jwts.parser()
//                    .setSigningKey(Constants.JWT_SECRET)
//                    .parseClaimsJws(token)
//                    .getBody();
            UserPrincipal lvActor = UserPrincipal.anonymousUser();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                if (!"anonymousUser".equalsIgnoreCase(authentication.getPrincipal().toString())) {
                    lvActor = (UserPrincipal) authentication.getPrincipal();
                }
            }
            this.userPrincipal = lvActor;
        } catch (Exception e) {
            throw new AppException();
        }
    }

    public void reset() {
        this.token = Constants.EMPTY;
        this.userPrincipal = UserPrincipal.anonymousUser();
    }
}