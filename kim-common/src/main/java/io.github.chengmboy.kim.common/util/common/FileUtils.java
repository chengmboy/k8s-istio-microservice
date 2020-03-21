package io.github.chengmboy.kim.common.util.common;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author cheng_mboy
 */
public class FileUtils {

    /**
     * 将一个大文件切割成小文件
     */
    public static void divide(File file, int maxSize, CapacityUnit c, String destDir) {
        /**
         * 校验文件
         * 根据maxSize 读取文件
         * 生成子文件
         * */
        if (!file.isFile()) {
            throw new IllegalArgumentException("文件错误");
        }
        try {
            List<ByteArrayOutputStream> files = new LinkedList<>();
            FileInputStream in = new FileInputStream(file);
            int bytes = maxSize * c.byteNum;
            ByteArrayOutputStream child = new ByteArrayOutputStream(bytes);
            byte[] buffer = new byte[bytes];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                child.write(buffer, 0, bytesRead);
                files.add(child);
                child = new ByteArrayOutputStream(bytes);
            }
            File dir = new File(destDir);
            if (!dir.exists()) {
                boolean create = dir.mkdirs();
                if (!create) {
                    if (!dir.exists()) {
                        throw new RuntimeException("目标路径无法创建");
                    }
                }
            }
            for (int i = 0; i < files.size(); i++) {
                String name = file.getName();
                String[] pros = name.split("\\.");
                String orginName = pros[0];
                String pName = "";
                if (pros.length > 1) {
                    pName = pros[1];
                }
                String fileName = String.format("%s/%s-%s.%s", destDir, orginName, i + 1, pName);
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(files.get(i).toByteArray());
                StreamUtils.copy(inputStream, fileOutputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("切割文件出错", e);
        }
    }

    enum CapacityUnit {
        KB(1024),
        B(1),
        MB(1024 * 1024);
        private final int byteNum;

        CapacityUnit(int byteNum) {
            this.byteNum = byteNum;
        }
    }
}
