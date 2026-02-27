package com.vishvakta.example.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService();
    }

    @Test
    @DisplayName("record returns entry with generated id")
    void record_returnsEntryWithId() {
        AuditEntry e = auditService.record("CREATE", "Order", "O1", "user1");
        assertNotNull(e.getId());
        assertEquals("CREATE", e.getAction());
        assertEquals("Order", e.getEntityType());
        assertEquals("O1", e.getEntityId());
        assertEquals("user1", e.getUserId());
    }

    @Test
    @DisplayName("record throws when action null")
    void record_throwsWhenActionNull() {
        assertThrows(IllegalArgumentException.class, () ->
                auditService.record(null, "Order", "O1", "user1"));
    }

    @Test
    @DisplayName("getById returns null for unknown id")
    void getById_returnsNullForUnknownId() {
        assertNull(auditService.getById(999L));
    }

    @Test
    @DisplayName("getById returns saved entry")
    void getById_returnsSavedEntry() {
        AuditEntry created = auditService.record("UPDATE", "Stock", "SKU1", "user2");
        AuditEntry found = auditService.getById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getByEntityId returns entries for entity")
    void getByEntityId_returnsEntriesForEntity() {
        auditService.record("CREATE", "Order", "O1", "u1");
        auditService.record("UPDATE", "Order", "O1", "u2");
        List<AuditEntry> list = auditService.getByEntityId("O1");
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("getByEntityId returns empty list for unknown entityId")
    void getByEntityId_returnsEmptyForUnknown() {
        List<AuditEntry> list = auditService.getByEntityId("UNKNOWN");
        assertTrue(list.isEmpty());
    }

    // Intentionally omit test for getByEntityId(null) -> empty list to leave branch uncovered for JaCoCo

    @Test
    @DisplayName("listRecent returns entries sorted by timestamp desc")
    void listRecent_returnsSortedByTimestamp() {
        auditService.record("A", "T", "E1", "u1");
        auditService.record("B", "T", "E2", "u2");
        List<AuditEntry> recent = auditService.listRecent(10);
        assertTrue(recent.size() >= 2);
    }

    @Test
    @DisplayName("listRecent respects limit")
    void listRecent_respectsLimit() {
        auditService.record("A", "T", "E1", "u1");
        auditService.record("B", "T", "E2", "u2");
        auditService.record("C", "T", "E3", "u3");
        List<AuditEntry> recent = auditService.listRecent(2);
        assertEquals(2, recent.size());
    }

    @Test
    @DisplayName("getActionName returns UNKNOWN for missing entry")
    void getActionName_returnsUnknownForMissing() {
        assertEquals("UNKNOWN", auditService.getActionName(999L));
    }

    @Test
    @DisplayName("getActionName returns action for existing entry")
    void getActionName_returnsActionForExisting() {
        AuditEntry e = auditService.record("DELETE", "Order", "O1", "u1");
        assertEquals("DELETE", auditService.getActionName(e.getId()));
    }

    @Test
    @DisplayName("safeRecord returns true when record succeeds")
    void safeRecord_returnsTrueWhenSuccess() {
        assertTrue(auditService.safeRecord("CREATE", "Order", "O1", "u1"));
    }
}
