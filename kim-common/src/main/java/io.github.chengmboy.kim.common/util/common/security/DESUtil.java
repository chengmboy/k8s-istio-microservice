package io.github.chengmboy.kim.common.util.common.security;


import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/**
 * @author cheng_mboy
 */
public class DESUtil {

    public static void main(String[] args) throws GeneralSecurityException {
        process();
    }

    private static void process() throws GeneralSecurityException {
        byte[] key = getKey();
        String s = Base64.getEncoder().encodeToString(key);
        System.out.println("key: " + s);

        String data = "我是重要的消息";
        System.out.println("发送方： " + data);

        System.out.println("-------------------");
        String s1 = Base64.getEncoder().encodeToString(encrypt(data.getBytes(), key));
        System.out.println("传输: " + s1);
        System.out.println("-------------------");


        byte[] decode = Base64.getDecoder().decode(s1);

        String s2 = new String(decrypt(decode, key));
        System.out.println("接收方: " + s2);
        System.out.println(data.equals(s2) ? "解密成功" : "失败");
    }

    public static byte[] getKey() throws GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        DESKeySpec keySpec = new DESKeySpec(bytes);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey key = factory.generateSecret(keySpec);
        return key.getEncoded();
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        DESKeySpec keySpec = new DESKeySpec(key);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey k = factory.generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        DESKeySpec keySpec = new DESKeySpec(key);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey k = factory.generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }
}
