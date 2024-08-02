package com.slow3586.micromarket.api.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Aspect
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@AuditDisabled
@EnableAspectJAutoProxy
@Slf4j
public class AuditAspect {
    @Lazy final Tracer tracer;
    Random random = new Random();
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Pointcut("within(com.slow3586.micromarket..*)")
    public void app() {}

    @Pointcut("@annotation(com.slow3586.micromarket.api.audit.AuditDisabled)")
    public void auditDisabled() {}

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void scheduled() {}

    @Pointcut("@within(org.springframework.stereotype.Component)")
    public void component() {}

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void service() {}

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {}

    @Pointcut("execution(* org.springframework.kafka.core.KafkaTemplate.send(..))")
    public void kafkaTemplate() {}

    @Pointcut("execution(* org.springframework.jdbc.core.JdbcTemplate.*(..))")
    public void jdbcTemplate() {}

    @Around("app() && !auditDisabled() && !scheduled() && (service() || restController()) || kafkaTemplate()")
    protected Object joinPoint(@NonNull ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final Instant start = Instant.now();
        final TraceContext context = tracer == null ? null : tracer.currentTraceContext().context();
        final AuditDto.AuditDtoBuilder auditDto = AuditDto.builder()
            .spanId(context == null ? null : context.spanId())
            .traceId(context == null ? null : context.traceId())
            .methodClass(proceedingJoinPoint.getSignature().getDeclaringTypeName())
            .methodName(proceedingJoinPoint.getSignature().getName())
            .methodArgs(Arrays.stream(proceedingJoinPoint.getArgs())
                .map(Objects::toString)
                .collect(Collectors.joining(", ")))
            .startTime(start);

        try {
            final Object result = proceedingJoinPoint.proceed();
            auditDto.methodResult(Objects.toString(result));
            return result;
        } catch (final Throwable exceptionWrapper) {
            final Throwable exception =
                exceptionWrapper instanceof UndeclaredThrowableException
                    ? ((UndeclaredThrowableException) exceptionWrapper).getUndeclaredThrowable()
                    : exceptionWrapper;
            auditDto.exceptionClass(exception.getClass().getName())
                .exceptionMessage(exception.getMessage())
                .exceptionStack(StringUtils.substring(
                    Arrays.toString(exception.getStackTrace()),
                    0,
                    8000).replaceAll(", ", "," + System.lineSeparator()));
            throw exception;
        } finally {
            final Instant end = Instant.now();
            log.info(objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                    auditDto.endTime(end)
                        .duration(Duration.between(start, end))
                        .startTime(Instant.now())
                        .build()));
        }
    }
}
