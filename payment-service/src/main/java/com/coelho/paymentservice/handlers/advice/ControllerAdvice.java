package com.coelho.paymentservice.handlers.advice;

import com.coelho.paymentservice.exceptions.BusinessException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    private final Tracer tracer;

    private final MeterRegistry meterRegistry;

    private final HttpServletRequest request;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<APIError> handleBusinessException(BusinessException businessException) {
        log.error("Handling", businessException);

        Collection<APIErrorMessage> messages = List.of(APIErrorMessage.builder()
                                                                      .code(businessException.getKey())
                                                                      .description(businessException
                                                                              .getKey())
                                                                      .build());

        return ResponseEntity.status(businessException.getHttpStatus())
                             .body(buildAPIErrorAndRegisterMetrics(messages));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException validException, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.error("Handling", validException);

        Collection<APIErrorMessage> messages = validException.getBindingResult().getAllErrors()
                                                             .stream()
                                                             .map(error -> APIErrorMessage.builder()
                                                                                          .code(error
                                                                                                  .getDefaultMessage())
                                                                                          .description(error
                                                                                                  .getDefaultMessage())
                                                                                          .build())
                                                             .collect(Collectors.toList());

        APIError apiError = buildAPIErrorAndRegisterMetrics(messages);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public APIError handleException(Exception exception) {
        log.error("Handling", exception);

        Collection<APIErrorMessage> messages = List.of(APIErrorMessage.builder()
                                                                      .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                                                                      .description(HttpStatus.INTERNAL_SERVER_ERROR
                                                                              .getReasonPhrase())
                                                                      .build());

        return buildAPIErrorAndRegisterMetrics(messages);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Handling", exception);

        Collection<APIErrorMessage> messages = List.of(APIErrorMessage.builder()
                                                                      .code(status.name())
                                                                      .description(status.getReasonPhrase())
                                                                      .build());

        APIError apiError = buildAPIErrorAndRegisterMetrics(messages);

        return super.handleExceptionInternal(exception, apiError, headers, status, request);
    }

    private String getTraceId() {
        if (Objects.nonNull(tracer) && Objects.nonNull(tracer.activeSpan()) &&
                Objects.nonNull(tracer.activeSpan().context())) {
            return tracer.activeSpan().context().toTraceId();
        } else {
            return null;
        }
    }

    private APIError buildAPIError(@NonNull Collection<APIErrorMessage> messages) {
        return APIError.builder()
                       .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                       .traceId(getTraceId())
                       .messages(messages)
                       .build();
    }

    private void registerMetrics(@NonNull APIError apiError) {
        // Set Jaeger Trace Error
        tracer.activeSpan().setTag(Tags.ERROR, Boolean.TRUE);

        // Prometheus Metrics
        Collection<Tag> tags = new ArrayList<>();
        tags.addAll(apiError.toTags());
        tags.add(Tag.of("method", request.getMethod()));
        tags.add(Tag.of("uri", request.getRequestURI()));

        meterRegistry.counter("http_server_request_error", tags).increment();
    }

    private APIError buildAPIErrorAndRegisterMetrics(@NonNull Collection<APIErrorMessage> messages) {
        APIError apiError = buildAPIError(messages);
        registerMetrics(apiError);

        return apiError;
    }

}
