package com.flowiee.pms.common.utils;

import com.flowiee.pms.base.Core;
import com.flowiee.pms.entity.system.SystemConfig;
import com.flowiee.pms.common.enumeration.ConfigCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

public class PasswordUtils {
    public static int DEFAULT_PASSWORD_LENGTH = 8;

    public static String encodePassword(String pwd) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(pwd);
    }

    public static int getPasswordLength() {
        SystemConfig pwdLengthCnf = Core.getSystemConfigs().get(ConfigCode.passwordLength);
        if (SysConfigUtils.isValid(pwdLengthCnf)) {
            return pwdLengthCnf.getIntValue();
        }
        return DEFAULT_PASSWORD_LENGTH;
    }

    public static String generatePassword() {
        int lvMaxPasswordLength = PasswordUtils.getPasswordLength();
        Random lvPasswordRandom = new Random(System.currentTimeMillis());

        char[] lvNewPassword = new char[lvMaxPasswordLength];
        String lvAlphabetCharacters = "ABCDEFGHJKLMNPRTUVWXYabcdefghijkmnprtuvwxy";
        String lvNumericCharacters = "346789";

        for (int i = 0; i < lvNewPassword.length; i++) {
            String lvPasswordCharacterRange = (i < lvNewPassword.length / 2 ? lvAlphabetCharacters : lvNumericCharacters);
            lvNewPassword[i] = lvPasswordCharacterRange.charAt(lvPasswordRandom.nextInt(lvPasswordCharacterRange.length()));
        }

        return new String(lvNewPassword);
    }
}