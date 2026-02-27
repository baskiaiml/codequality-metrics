package com.vishvakta.example.metrics;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Audit service with deliberate SpotBugs/PMD triggers for quality metrics demo.
 */
@Service
public class AuditService {

    // Deliberate: unused field - SpotBugs UUF_UNUSED_FIELD
    private static final int MAX_AUDIT_ENTRIES = 50000;

    private final Map<Long, AuditEntry> entries = new ConcurrentHashMap<>();
    private long nextId = 1L;

    public AuditEntry record(String action, String entityType, String entityId, String userId) {
        if (action == null || entityType == null || entityId == null) {
            throw new IllegalArgumentException("action, entityType, entityId must not be null");
        }
        AuditEntry e = new AuditEntry(nextId++, action, entityType, entityId, Instant.now(), userId);
        entries.put(e.getId(), e);
        return e;
    }

    public AuditEntry getById(Long id) {
        return entries.get(id);
    }

    public List<AuditEntry> getByEntityId(String entityId) {
        if (entityId == null) {
            return new ArrayList<>();
        }
        return entries.values().stream()
                .filter(e -> entityId.equals(e.getEntityId()))
                .collect(Collectors.toList());
    }

    public List<AuditEntry> listRecent(int limit) {
        return entries.values().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Deliberate: comparing strings with == - SpotBugs ES_COMPARING_STRINGS_WITH_EQ
     */
    public boolean isAuditForEntity(Long auditId, String entityId) {
        AuditEntry e = entries.get(auditId);
        if (e == null) {
            return false;
        }
        return e.getEntityId() == entityId;
    }

    /**
     * Deliberate: dead store - DLS_DEAD_LOCAL_STORE
     */
    public String getActionName(Long id) {
        AuditEntry e = entries.get(id);
        String result = "UNKNOWN";
        if (e != null) {
            result = e.getAction();
        }
        String unused = result; // dead store
        return result;
    }

    /**
     * Deliberate: empty catch - PMD EmptyCatchBlock
     */
    public boolean safeRecord(String action, String entityType, String entityId, String userId) {
        try {
            record(action, entityType, entityId, userId);
            return true;
        } catch (IllegalArgumentException ex) {
            // empty catch for PMD demo
        }
        return false;
    }
}
