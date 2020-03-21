package io.github.chengmboy.kim.common.bean.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.chengmboy.kim.common.constants.SecurityConstants;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String user = request.getHeader(SecurityConstants.USER_HEADER);
            requestTemplate.header(SecurityConstants.USER_HEADER, user);
        }
    }
}
