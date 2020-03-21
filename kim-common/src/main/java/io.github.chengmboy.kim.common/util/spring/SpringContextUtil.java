package io.github.chengmboy.kim.common.util.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author cheng_mboy
 */
@Component
public class SpringContextUtil {

    private static ApplicationContext SPRING_CONTEXT;

    public SpringContextUtil(ApplicationContext applicationContext) {
        SPRING_CONTEXT = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return SPRING_CONTEXT.getBean(clazz);
    }
}
