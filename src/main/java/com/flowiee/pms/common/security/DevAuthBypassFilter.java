package com.flowiee.pms.common.security;

import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.CoreUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@ConditionalOnProperty(name = "system.login.bypass", havingValue = "true")
public class DevAuthBypassFilter implements Filter {
    @Value("${system.login.bypass}")
    private boolean mvSystemByPass;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            CustomHttpServletRequestWrapper wrappedRequest = new CustomHttpServletRequestWrapper((HttpServletRequest) request);
            String servletPath = wrappedRequest.getServletPath();

            if (WebSecurityConfig.getAuthEndPoint().equals(servletPath)) {
                if (mvSystemByPass) {
                    String username = CoreUtils.trim(wrappedRequest.getParameter("username"));
                    if (CoreUtils.isNullStr(username)) {
                        username = Constants.ADMINISTRATOR;
                    }
                    wrappedRequest.setParameter("username", username);
                    wrappedRequest.setParameter("password", CommonUtils.defaultNewPassword);
                }
            }

            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}