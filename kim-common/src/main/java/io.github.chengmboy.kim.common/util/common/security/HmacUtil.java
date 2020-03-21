package io.github.chengmboy.kim.common.util.common.security;

import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 该类提供了“消息验证码”（MAC）算法的功能。
 * MAC提供了一种基于秘密密钥来检查在不可靠介质中传输或存储在信息中的信息的完整性的方法。
 * 通常，在共享密钥的两方之间使用消息认证码，以验证在这些方之间传输的信息。
 * <p>
 * 基于加密散列函数的MAC机制被称为HMAC。 HMAC可以与任何加密散列函数一起使用，
 * 例如MD5或SHA-1与秘密共享密钥的组合。 HMAC在RFC 2104中规定。
 * <p>
 * Java平台的每个实现都需要支持以下标准Mac算法：
 * <p>
 * <ul>
 * <li>{@code HmacMD5}</li>
 * <li>{@code HmacSHA1}</li>
 * <li>{@code HmacSHA256}</li>
 * </ul>
 *
 * @author cheng_mboy
 */
public class HmacUtil {

    public static final String MD5 = "HmacMD5";
    public static final String SHA1 = "HmacSHA1";
    public static final String SHA256 = "HmacSHA256";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static void main(String[] args) throws GeneralSecurityException {
        System.out.println(sha1Encrypt2HexString("abc".getBytes(), "key".getBytes()));
        System.out.println(md5Encrypt2HexString("abc".getBytes(), "key".getBytes()));
    }

    public static byte[] encrypt(byte[] data, byte[] key, String algorithm) throws GeneralSecurityException {
        SecretKeySpec secret = new SecretKeySpec(key, algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secret);
        return mac.doFinal(data);
    }

    public static byte[] md5Encrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        return encrypt(data, key, MD5);
    }

    public static byte[] sha1Encrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        return encrypt(data, key, SHA1);
    }

    public static byte[] sha256Encrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        return encrypt(data, key, SHA256);
    }

    public static String md5Encrypt2HexString(byte[] data, byte[] key) throws GeneralSecurityException {
        byte[] bytes = encrypt(data, key, MD5);
        return toHexString(bytes);
    }

    public static String sha1Encrypt2HexString(byte[] data, byte[] key) throws GeneralSecurityException {
        byte[] bytes = encrypt(data, key, SHA1);
        return toHexString(bytes);
    }

    public static String sha256Encrypt2HexString(byte[] data, byte[] key) throws GeneralSecurityException {
        byte[] bytes = encrypt(data, key, SHA256);
        return toHexString(bytes);
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder r = new StringBuilder();
        for (byte b : bytes) {
            r.append(HEX_DIGITS[(b & 0xf0) >> 4])
                    .append(HEX_DIGITS[b & 0xf]);
        }
        return r.toString();
    }
}
