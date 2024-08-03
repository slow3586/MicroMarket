package com.slow3586.micromarket.api.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditDto {
    Instant startTime;
    Instant endTime;
    Duration duration;
    String spanId;
    String traceId;
    String methodClass;
    String methodName;
    String methodArgs;
    String methodResult;
    String exceptionClass;
    String exceptionMessage;
    List<String> exceptionStack;
}
