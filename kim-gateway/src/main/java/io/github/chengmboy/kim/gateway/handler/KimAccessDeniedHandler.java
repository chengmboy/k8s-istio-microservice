package io.github.chengmboy.kim.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.chengmboy.kim.common.constants.CommonConstant;
import io.github.chengmboy.kim.common.exception.KimDeniedException;
import io.github.chengmboy.kim.common.web.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Slf4j
@Component
public class KimAccessDeniedHandler extends OAuth2AccessDeniedHandler {
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 授权拒绝处理，使用R包装
     *
     * @param request       request
     * @param response      response
     * @param authException authException
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException authException) throws IOException, ServletException {
        log.info("授权失败，禁止访问 {}", request.getRequestURI());
        response.setCharacterEncoding(CommonConstant.UTF8);
        response.setContentType(CommonConstant.CONTENT_TYPE);
        Response<String> result = Response.error(new KimDeniedException("授权失败，禁止访问"));
        response.setStatus(HttpStatus.SC_FORBIDDEN);
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(result));
    }
}
