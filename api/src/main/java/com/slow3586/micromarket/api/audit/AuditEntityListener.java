package com.slow3586.micromarket.api.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditEntityListener {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @PrePersist
    public void prePersist(Object entity) {
        try {
            log.info("PERSIST {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity));
        } catch (Exception ignored) {
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        try {
            log.info("UPDATE {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity));
        } catch (Exception ignored) {
        }
    }

    @PostLoad
    public void postLoad(Object entity) {
        try {
            log.info("LOAD {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity));
        } catch (Exception ignored) {
        }
    }
}
