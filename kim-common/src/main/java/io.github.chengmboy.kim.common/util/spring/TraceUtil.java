package io.github.chengmboy.kim.common.util.spring;


import brave.Span;
import brave.Tracer;
import brave.Tracing;
import org.springframework.util.StringUtils;

/**
 * @author cheng_mboy
 */
public class TraceUtil {

    private static ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    public static ThreadLocal<Span> SPAN = new ThreadLocal<>();

    public static Tracer getTracer() {
        try {
            return Tracing.currentTracer();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTraceId() {
        Tracer tracer = Tracing.currentTracer();
        String traceId;
        Span span = tracer.currentSpan();
        if (span != null) {
            traceId = Long.toHexString(span.context().traceId());
        } else {
            traceId = StringUtils.isEmpty(TRACE_ID.get()) ? "" : TRACE_ID.get();
        }
        return traceId;
    }
}
