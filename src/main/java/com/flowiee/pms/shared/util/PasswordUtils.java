package com.flowiee.pms.shared.util;

import com.flowiee.pms.shared.base.FlwSys;
import com.flowiee.pms.system.entity.SystemConfig;
import com.flowiee.pms.system.enums.ConfigCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

public class PasswordUtils {
    public static int DEFAULT_PASSWORD_LENGTH = 8;

    public static String encodePassword(String pwd) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(pwd);
    }

    public static int getPasswordLength() {
        SystemConfig pwdLengthCnf = FlwSys.getSystemConfigs().get(ConfigCode.passwordLength);
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