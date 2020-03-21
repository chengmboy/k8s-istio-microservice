package io.github.chengmboy.kim.common.util.common;

/**
 * @author cheng_mboy
 */
public class ArrayUtils {

    /**
     * 遍历二维数组
     */
    public static void traverse(Object[][] a) {
        for (Object[] b : a) {
            for (Object c : b) {
                System.out.printf("%-10s", c);
            }
            System.out.println();
        }
    }
}
