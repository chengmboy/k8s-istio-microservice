package io.github.chengmboy.kim.common.bean.interceptor;

import io.github.chengmboy.kim.common.constants.SecurityConstants;
import io.github.chengmboy.kim.common.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String username = request.getHeader(SecurityConstants.USER_HEADER);
        if (StringUtils.isEmpty(username)) {
            log.debug("未登录");
        }
        UserUtils.setUser(username);
        return true;
    }
}
