package io.github.chengmboy.kim.common.util.common;

/**
 * @author cheng_mboy
 */
public class LowCamelUtils {

    private static final char A = 'A';
    private static final char Z = 'Z';
    private static final char UNDER_LINE = '_';

    /**
     * 将小驼峰命名的字符串变成下划线命名
     *
     * @param str 小驼峰命名的字符串
     * @return 转换成下划线的字符串
     */
    public static String toUnderLine(String str) {
        validateLowCamel(str);
        char[] chars = str.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char c : chars) {
            if (c >= A && c <= Z) {
                builder.append('_');
                builder.append((char) (c + 32));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static void validateLowCamel(String str) {
        char c = str.charAt(0);
        if (c >= A && c <= Z) {
            throw new IllegalArgumentException("小驼峰命名的字符串首字符不能为大写字符：" + str);
        }
    }

    /**
     * 将下划线命名的转换成小驼峰命名
     *
     * @param str 下划线命名方式的字符串
     * @return 小驼峰命名的字符串
     */
    public static String toLowCamel(String str) {
        validateUnderLine(str);
        char[] chars = str.toCharArray();
        StringBuilder builder = new StringBuilder();
        boolean flag = false;
        for (char c : chars) {
            if (UNDER_LINE == c) {
                flag = true;
            } else if (flag) {
                builder.append((char) (c - 32));
                flag = false;
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static void validateUnderLine(String str) {
        char c = str.charAt(0);
        boolean max = (c >= A && c <= Z);
        if (UNDER_LINE == c || max) {
            throw new IllegalArgumentException("下划线命名格式的字符串首字符不能为_或者大写字符：" + str);
        }
    }

    public static void main(String[] args) {
        System.out.println(toUnderLine("aaBbCc"));
        System.out.println(toLowCamel("aa_bb_cc"));
    }
}
