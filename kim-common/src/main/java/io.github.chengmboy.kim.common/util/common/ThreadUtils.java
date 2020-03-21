package io.github.chengmboy.kim.common.util.common;

import java.util.concurrent.*;

/**
 * @author cheng_mboy
 */
public class ThreadUtils {

    private static final ThreadFactory THREAD_FACTORY = Executors.defaultThreadFactory();

    public static ThreadPoolExecutor getThreadPoolExecutor(int threadCount, int blockQueueSize) {
        return new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(blockQueueSize),
                THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
    }
}
