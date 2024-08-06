package com.slow3586.micromarket.api.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
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
    final ObjectMapper objectMapper;

    @Pointcut("within(com.slow3586.micromarket..*)")
    public void app() {}

    @Pointcut("@annotation(com.slow3586.micromarket.api.audit.AuditDisabled)")
    public void auditDisabled() {}

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void scheduled() {}

    @Pointcut("@within(org.springframework.stereotype.Component)")
    public void component() {}

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repository() {}

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void service() {}

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {}

    @Pointcut("execution(* org.springframework.kafka.core.KafkaTemplate.send(..))")
    public void kafkaTemplate() {}

    @Around("app() " +
        "&& !auditDisabled() " +
        "&& !scheduled() " +
        "&& (repository() || service() || restController()) " +
        "|| kafkaTemplate()")
    protected Object joinPoint(@NonNull ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final Instant start = Instant.now();
        final TraceContext context = tracer == null ? null : tracer.currentTraceContext().context();
        final MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        final AuditDto.AuditDtoBuilder auditDto = AuditDto.builder()
            .spanId(context == null ? null : context.spanId())
            .traceId(context == null ? null : context.traceId())
            .methodClass(methodSignature.getDeclaringTypeName())
            .methodName(methodSignature.getName())
            .startTime(start);

        if (proceedingJoinPoint.getArgs().length > 0) {
            auditDto.methodArgs(Arrays.stream(proceedingJoinPoint.getArgs())
                .map(Objects::toString)
                .collect(Collectors.joining(", ")));
        }

        try {
            final Object result = proceedingJoinPoint.proceed();
            if (!methodSignature.getReturnType().equals(Void.TYPE)) {
                auditDto.methodResult(Objects.toString(result));
            }
            return result;
        } catch (final Throwable exceptionWrapper) {
            final Throwable exception =
                exceptionWrapper instanceof UndeclaredThrowableException
                    ? ((UndeclaredThrowableException) exceptionWrapper).getUndeclaredThrowable()
                    : exceptionWrapper;
            StringJoiner message = new StringJoiner("; ");
            Throwable cause = exception.getCause();
            while (cause != null) {
                message.add(cause.getMessage());
                cause = cause.getCause();
            }
            auditDto.exceptionClass(exception.getClass().getName())
                .exceptionMessage(message.toString())
                .exceptionStack(ExceptionUtils.getStackTrace(exception));
            throw exception;
        } finally {
            final Instant end = Instant.now();
            log.info(objectMapper
                .writeValueAsString(
                    auditDto.endTime(end)
                        .duration(Duration.between(start, end))
                        .startTime(Instant.now())
                        .build()));
        }
    }
}
