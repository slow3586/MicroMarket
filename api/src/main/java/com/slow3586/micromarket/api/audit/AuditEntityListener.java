package com.slow3586.micromarket.api.audit;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditEntityListener  {
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
