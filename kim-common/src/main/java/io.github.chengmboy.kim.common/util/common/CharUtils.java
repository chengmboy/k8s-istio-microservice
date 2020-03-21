package io.github.chengmboy.kim.common.util.common;

import java.util.Random;

/**
 * @author cheng_mboy
 */
public class CharUtils {

    public final static int ALPHABET_NUMBER = 26;
    public static char[] alphabets = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0','1','2','3','4','5','6','7','8','9'};

    /**
     * 随机返回一个英文字符
     */
    public static char next() {
        Random random = new Random();
        int i = random.nextInt(ALPHABET_NUMBER * 2+10);
        return alphabets[i];
    }

    /**
     * 随机返回一个大写字符
     */
    public static char nextUpperCase() {
        Random random = new Random();
        int i = random.nextInt(ALPHABET_NUMBER);
        return alphabets[i];
    }

    /**
     * 随机返回一个小写字符
     */
    public static char nextLowerCase() {
        Random random = new Random();
        int i = ALPHABET_NUMBER + random.nextInt(ALPHABET_NUMBER);
        return alphabets[i];
    }

    /**
     * 随机返回一个字符串
     */
    public static String nextString(int length) {
        StringBuilder result = new StringBuilder();
        while (length > 0) {
            result.append(next());
            length--;
        }
        return result.toString();
    }

    /**
     * 随机返回一个大写的字符串
     */
    public static String nextUpperCaseString(int length) {
        StringBuilder result = new StringBuilder();
        while (length > 0) {
            result.append(nextUpperCase());
            length--;
        }
        return result.toString();
    }

    /**
     * 随机返回一个小写的字符串
     */
    public static String nextLowerCaseString(int length) {
        StringBuilder result = new StringBuilder();
        while (length > 0) {
            result.append(nextLowerCase());
            length--;
        }
        return result.toString();
    }

    /**
     * 随机返回一个大写的字符串，不含重复字母
     *
     * @param length 字符串长度
     */
    public static String nextNoRepetitionUpperCaseString(int length) {
        if (length < 1 || length > ALPHABET_NUMBER) {
            throw new IllegalArgumentException("length应该大于0小于27");
        }
        StringBuilder result = new StringBuilder();
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            repetition:
            do {
                char c = nextUpperCase();
                if (i - 1 < 0) {
                    chars[i] = c;
                } else {
                    for (int j = 0; j < i; j++) {
                        if (chars[j] == c) {
                            continue repetition;
                        }
                    }
                    chars[i] = c;
                }
                break;
            } while (true);
        }
        result.append(chars);
        return result.toString();
    }

    /**
     * 随机返回一个小写的字符串，不含重复字母
     *
     * @param length 字符串长度
     */
    public static String nextNoRepetitionLowerCaseString(int length) {
        if (length < 1 || length > ALPHABET_NUMBER) {
            throw new IllegalArgumentException("length应该大于0小于27");
        }
        StringBuilder result = new StringBuilder();
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            repetition:
            do {
                char c = nextLowerCase();
                if (i - 1 < 0) {
                    chars[i] = c;
                } else {
                    for (int j = 0; j < i; j++) {
                        if (chars[j] == c) {
                            continue repetition;
                        }
                    }
                    chars[i] = c;
                }
                break;
            } while (true);
        }
        result.append(chars);
        return result.toString();
    }
}
