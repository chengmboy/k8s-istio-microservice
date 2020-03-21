package io.github.chengmboy.kim.common.util.common;

/**
 * @author cheng_mboy
 */
public class BitUtils {

    /**
     * 获取所给数字在某位的值
     *
     * @param value 所给数字
     * @param index 位数
     * @return index位的值 0 or 1
     */
    public static int bitValue(int value, int index) {
        return value >> (index - 1) & 1;
    }
}
