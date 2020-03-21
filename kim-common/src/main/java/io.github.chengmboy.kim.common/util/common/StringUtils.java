package io.github.chengmboy.kim.common.util.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cheng_mboy
 */
public class StringUtils {

    public static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字符串倒序重组
     */
    public static String reverse(String src) {
        char[] chars = src.toCharArray();
        char[] newChars = new char[src.length()];
        int oldIndex = chars.length - 1;
        for (int i = 0; i < newChars.length; i++) {
            newChars[i] = chars[oldIndex];
            oldIndex--;
        }
        return new String(newChars);
    }

    /**
     * 将字节数组转换成16进制字符串
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder r = new StringBuilder();
        for (byte b : bytes) {
            r.append(HEX_DIGITS[(b & 0xf0) >> 4])
                    .append(HEX_DIGITS[b & 0xf]);
        }
        return r.toString();
    }

    /**
     * 将字符串第一个字符变成大写
     */
    public static String upperCaseFirst(String str) {
        char c = str.charAt(0);
        if (!Character.isUpperCase(c)) {
            str = Character.toUpperCase(c) + str.substring(1, str.length());
        }
        return str;
    }

    /**
     * 将字符串第一个字符变成小写
     */
    public static String lowerCaseFirst(String str) {
        char c = str.charAt(0);
        if (!Character.isLowerCase(c)) {
            str = Character.toLowerCase(c) + str.substring(1, str.length());
        }
        return str;
    }

    public static BigDecimal ipToBigDecimal(String addr) throws UnknownHostException {
        InetAddress a = InetAddress.getByName(addr);
        byte[] bytes = a.getAddress();
        return new BigDecimal(new BigInteger(1, bytes));
    }

    public static String numberToIP(BigDecimal ipNumber) throws UnknownHostException {
        BigInteger ip = ipNumber.toBigInteger();
        String ipString = "";
        byte[] bytes = ip.toByteArray();
        if (bytes.length > 4) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        InetAddress address = InetAddress.getByAddress(bytes);
        if (address instanceof Inet4Address) {
            return longToip(ip.longValue());
        }
        BigInteger a = new BigInteger("FFFF", 16);
        for (int i = 0; i < 8; i++) {
            ipString = ip.and(a).toString(16) + ":" + ipString;
            ip = ip.shiftRight(16);
        }
        return ipString.substring(0, ipString.length() - 1);
    }

    public static BigInteger ipToBigInteger(String addr) throws UnknownHostException {
        InetAddress a = InetAddress.getByName(addr);
        byte[] bytes = a.getAddress();
        System.out.println(a.getHostAddress());
        return new BigInteger(1, bytes);
    }

    public static String numberToIPv6(BigInteger ipNumber) {
        String ipString = "";
        BigInteger a = new BigInteger("FFFF", 16);
        for (int i = 0; i < 8; i++) {
            ipString = ipNumber.and(a).toString(16) + ":" + ipString;

            ipNumber = ipNumber.shiftRight(16);
        }
        return ipString.substring(0, ipString.length() - 1);

    }

    public static String longToip(long ipLong) {
        long[] mask = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        StringBuilder ipInfo = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            long num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0) {
                ipInfo.insert(0, ".");
            }
            ipInfo.insert(0, Long.toString(num, 10));
        }
        return ipInfo.toString();
    }

    public static long ipToLong(String strIP) {
        try {
            if (strIP == null || strIP.length() == 0) {
                return 0L;
            }
            long[] ip = new long[4];
            int position1 = strIP.indexOf(".");
            int position2 = strIP.indexOf(".", position1 + 1);
            int position3 = strIP.indexOf(".", position2 + 1);
            ip[0] = Long.parseLong(strIP.substring(0, position1));
            ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
            ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
            ip[3] = Long.parseLong(strIP.substring(position3 + 1));
            return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * 寻找不重复最长子串长度
     */
    public int lengthOfLongestSubstring(String s) {
        char[] chars = s.toCharArray();
        Set<Character> longest = new HashSet<>();
        int r = 0;
        for (int i = 0; i < chars.length; i++) {
            if (longest.contains(chars[i])) {
                r = Math.max(longest.size(), r);
                longest = new HashSet<>();
            }
            longest.add(chars[i]);
        }
        return r;
    }

    /**
     * 寻找不重复最长子串
     */
    public String longestSubstring(String s) {
        char[] chars = s.toCharArray();
        Set<Character> longest = new HashSet<>();
        StringBuilder sub = new StringBuilder();
        int r = 0;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (longest.contains(chars[i])) {
                if (longest.size() > r) {
                    r = longest.size();
                    result = sub;
                }
                longest = new HashSet<>();
                sub = new StringBuilder();
            }
            longest.add(chars[i]);
            sub.append(chars[i]);
        }
        return result.toString();
    }

    public static String getRandomHexStr() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[3];
        random.nextBytes(bytes);

        return toHexString(bytes);
    }

}
