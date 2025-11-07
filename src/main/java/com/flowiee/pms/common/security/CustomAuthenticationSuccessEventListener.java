package com.flowiee.pms.common.security;

import com.flowiee.pms.common.base.FlwSys;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.modules.system.dto.GeoLocationResult;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.modules.system.repository.SystemLogRepository;
import com.flowiee.pms.modules.system.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final AccountRepository mvAccountRepository;
    private final SystemLogRepository mvSystemLogRepository;
    private final GeoLocationService mvGeoLocationService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication lvAuthentication = event.getAuthentication();
        if (lvAuthentication == null) {
            return;
        }

        UserPrincipal userPrincipal = (UserPrincipal) lvAuthentication.getPrincipal();
        Account account = mvAccountRepository.findByUsername(userPrincipal.getUsername());
        if (account != null) {
            // Reset fail login count
            if (account.getFailLogonCount() > 0) {
                account.setFailLogonCount(0);
                mvAccountRepository.save(account);
            }

            Object lvAuthDetails = lvAuthentication.getDetails();
            if (lvAuthDetails instanceof WebAuthenticationDetails lvWebAuthenticationDetails) {
                userPrincipal.setIp(lvWebAuthenticationDetails.getRemoteAddress());
            }

            // Get ip
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (attrs != null) ? attrs.getRequest() : null;
            String clientIp = null;
            if (request != null) {
                clientIp = extractClientIp(request);
            } else {
                Object details = lvAuthentication.getDetails();
                if (details instanceof WebAuthenticationDetails webDetails) {
                    clientIp = webDetails.getRemoteAddress();
                }
            }
            userPrincipal.setIp(clientIp);

            //Get location
            String lvIpLocation = null;
            SystemConfig lvTrackRequestLocation = FlwSys.getSystemConfigs().get(ConfigCode.trackRequestLocation);
            if (SysConfigUtils.isValid(lvTrackRequestLocation) && SysConfigUtils.isYesOption(lvTrackRequestLocation)) {
                GeoLocationResult lvGeoLocationResult = mvGeoLocationService.lookup(clientIp);
                String lvCountry = lvGeoLocationResult.getCountry();
                String lvCity = lvGeoLocationResult.getCity();
                lvIpLocation = CoreUtils.isNullStr(lvCountry) ? null : String.format("Country: %s, city: %s", lvCountry, lvCity);
                userPrincipal.setLocation(lvIpLocation);
            }

            // Create system log
            SystemLog lvSystemLog = SystemLog.builder()
                    .module(MODULE.SYSTEM.name())
                    .function(ACTION.SYS_LOGIN.name())
                    .object(MasterObject.Account.name())
                    .mode(LogType.LI.name())
                    .content(account.getUsername() + " login")
                    .title("Login")
                    .account(account)
                    .ip(userPrincipal.getIp())
                    .location(lvIpLocation)
                    .build();
            lvSystemLog.setCreatedBy(account.getId());

            mvSystemLogRepository.save(lvSystemLog);
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip;

        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("CF-Connecting-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }
}