package io.github.chengmboy.kim.common.util.common.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import io.github.chengmboy.kim.common.util.common.CharUtils;
import lombok.Data;

/**
 * @author cheng_mboy
 */
public class PasswordUtil {

    public static Password generate(String passwordSrc) throws GeneralSecurityException {
        String salt = getRandomSalt();
        return generate(passwordSrc, salt);
    }
    public static Password generate(String passwordSrc ,String salt) throws GeneralSecurityException {
        String pwd = HmacUtil.sha256Encrypt2HexString(passwordSrc.getBytes(StandardCharsets.UTF_8),
                salt.getBytes(StandardCharsets.UTF_8));
        Password password = new Password();
        password.setPassword(pwd);
        password.setSalt(salt);
        return password;
    }


    public static boolean validate(String passwordSrc, String password, String salt) throws GeneralSecurityException {
        Password pwd = generate(passwordSrc, salt);
        return password.equals(pwd.getPassword());
    }

    public static String getRandomSalt() {
        return CharUtils.nextString(8);
    }


    @Data
   public static class Password{
        String password;
        String salt;
    }
}
