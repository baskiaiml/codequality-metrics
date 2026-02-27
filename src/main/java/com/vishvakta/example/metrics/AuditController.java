package com.vishvakta.example.metrics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<AuditEntry> record(
            @RequestParam String action,
            @RequestParam String entityType,
            @RequestParam String entityId,
            @RequestParam(required = false) String userId) {
        try {
            AuditEntry e = auditService.record(action, entityType, entityId, userId != null ? userId : "system");
            return ResponseEntity.status(HttpStatus.CREATED).body(e);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditEntry> getById(@PathVariable Long id) {
        AuditEntry e = auditService.getById(id);
        if (e == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(e);
    }

    @GetMapping(params = "entityId")
    public ResponseEntity<List<AuditEntry>> getByEntityId(@RequestParam String entityId) {
        List<AuditEntry> list = auditService.getByEntityId(entityId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditEntry>> listRecent(
            @RequestParam(defaultValue = "10") int limit) {
        List<AuditEntry> list = auditService.listRecent(limit);
        return ResponseEntity.ok(list);
    }
}
