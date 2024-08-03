package com.slow3586.micromarket.api.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditEntityListener  {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @PrePersist
    public void prePersist(Object entity) {
    }

    @PreUpdate
    public void preUpdate(Object entity) {
    }

    @PostLoad
    public void postLoad(Object entity) {
    }
}
