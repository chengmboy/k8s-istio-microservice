package io.github.chengmboy.kim.common.util.common.security;

import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * @author cheng_mboy
 */
public class TimeSercret {

    public static void main(String[] args) throws GeneralSecurityException, InterruptedException {
        byte[] key = DESUtil.getKey();
        String secret = Base64.getEncoder().encodeToString(key);
        System.out.println(secret);
        System.out.println(generateTimeCode(secret));
    }

    public static String generateTimeCode(String key) {
        try {
            //5秒有效
            int expire = 5;
            String s = key + System.currentTimeMillis() / expire / 1000;
            byte[] digest = MessageDigestUtil.md5Digest(s.getBytes());
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < expire; i++) {
                code.append((digest[i] & 0xff) % 9);
            }
            return code.toString();
        } catch (Exception e) {
            throw new RuntimeException("获取时间码失败!", e);
        }
    }
}
