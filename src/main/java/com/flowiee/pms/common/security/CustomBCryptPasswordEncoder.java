package com.flowiee.pms.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder {
    @Value("${system.login.bypass}")
    private boolean mvEnableByPass;

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String lvProfile = System.getProperty("spring.profiles.active");
        if ("dev".equals(lvProfile) && this.mvEnableByPass) {
            return true;
        }
        return super.matches(rawPassword, encodedPassword);
    }
}