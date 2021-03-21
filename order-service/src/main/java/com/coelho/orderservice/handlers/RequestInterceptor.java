package com.coelho.orderservice.handlers;

import io.opentracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {

    private final ThreadLocal<StopWatch> stopWatch = ThreadLocal.withInitial(StopWatch::new);

    private final Tracer tracer;

    @Value("${spring.application.request-interceptor.threshold}")
    private long threshold;

    @Value("${spring.application.request-interceptor.enabled}")
    private boolean enabled;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object body) throws Exception {

        stopWatch.get().start();

        if (Objects.nonNull(tracer) && Objects.nonNull(tracer.activeSpan()) &&
                Objects.nonNull(tracer.activeSpan().context())) {
            String traceId = tracer.activeSpan().context().toTraceId();
            MDC.put("traceId", traceId);
            MDC.put("spanId", tracer.activeSpan().context().toSpanId());
            response.addHeader("traceId", traceId);
        }

        if (enabled) {
            log.info("Received request: {} {}", request.getMethod(), request.getRequestURI());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex) throws Exception {

        StopWatch stopWatch = this.stopWatch.get();
        stopWatch.stop();

        long endTime = stopWatch.getTotalTimeMillis();
        if (enabled) {
            if (endTime > threshold) {
                log.warn("Finished request {} {}. Time spent is very high: {}ms", request.getMethod(),
                        request.getRequestURI(), endTime);
            } else {
                log.info("Finished request {} {}. Time spent: {}ms", request.getMethod(),
                        request.getRequestURI(), endTime);
            }
        }

        this.stopWatch.set(new StopWatch());
        MDC.clear();
    }

}