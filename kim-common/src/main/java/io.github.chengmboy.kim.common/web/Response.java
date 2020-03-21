package io.github.chengmboy.kim.common.web;

import io.github.chengmboy.kim.common.util.spring.TraceUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author cheng_mboy
 */
@Data
@AllArgsConstructor
public class Response<T> {

    private String requestId;
    private int code;
    private String message;
    private T data;

    public static <T> Response<T> ok(T data) {
        return new Response<>(TraceUtil.getTraceId(), HttpStatus.OK.value(), null, data);
    }
    public static <T> Response<T> ok() {
        return new Response<>(TraceUtil.getTraceId(), HttpStatus.OK.value(), null, null);
    }

    public static <T> Response<T> error(Throwable e) {
        return new Response<>(TraceUtil.getTraceId(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
    }
}
