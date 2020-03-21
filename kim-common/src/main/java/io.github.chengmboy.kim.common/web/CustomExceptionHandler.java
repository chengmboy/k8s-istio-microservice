package io.github.chengmboy.kim.common.web;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author cheng_mboy
 */
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<String> handleGeneraException(Throwable ex, HttpServletRequest request) {
        log.error("catch exception:{} ",ex);
        Response response = Response.error(ex);
        String bodyStr = JSONObject.toJSONString(response);
        return new ResponseEntity<>(bodyStr, HttpStatus.OK);
    }
}
