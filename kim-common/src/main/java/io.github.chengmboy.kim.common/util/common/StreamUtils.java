package io.github.chengmboy.kim.common.util.common;

import java.io.*;

/**
 * @author cheng_mboy
 */
public class StreamUtils {

    private final static int BUFFER_SIZE = 8192;

    /**
     * 复制InputStream内容到OutputStream
     *
     * @param in  复制的数据来源输入流
     * @param out 复制到的输出流
     * @return 总共复制的字节数
     * @throws IOException 发生IO错误
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }

    /**
     * 读取固定大小字节数
     *
     * @param in  复制的数据来源输入流
     * @param out 复制到的输出流
     * @return 总共复制的字节数
     * @throws IOException 发生IO错误
     */
    public static int copy(InputStream in, OutputStream out, int readCount) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            if (byteCount + bytesRead > readCount) {
                int least = readCount - byteCount;
                out.write(buffer, 0, least);
                byteCount += least;
            } else {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
        }
        out.flush();
        return byteCount;
    }

    /**
     * 序列化实现深度克隆，需要克隆对象及内部对象都必须实现Serializable接口
     *
     * @param src 需要克隆的对象
     * @return 克隆后对象
     * @throws Exception 克隆失败
     */
    public static <T> T deepClone(T src) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(output)) {
            outputStream.writeObject(src);
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(output.toByteArray()));
            return (T) inputStream.readObject();
        }
    }
}
