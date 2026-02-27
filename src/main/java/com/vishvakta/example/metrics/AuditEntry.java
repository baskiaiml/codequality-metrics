package com.vishvakta.example.metrics;

import java.time.Instant;
import java.util.Objects;

public class AuditEntry {

    private final Long id;
    private final String action;
    private final String entityType;
    private final String entityId;
    private final Instant timestamp;
    private final String userId;

    public AuditEntry(Long id, String action, String entityType, String entityId, Instant timestamp, String userId) {
        this.id = id;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuditEntry that = (AuditEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
