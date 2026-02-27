package com.vishvakta.example.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService.record("CREATE", "Order", "O1", "user1");
    }

    @Test
    @DisplayName("POST /api/audit creates entry and returns 201")
    void record_returns201AndBody() throws Exception {
        mockMvc.perform(post("/api/audit")
                        .param("action", "UPDATE")
                        .param("entityType", "Stock")
                        .param("entityId", "SKU1")
                        .param("userId", "user2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.action", org.hamcrest.Matchers.is("UPDATE")))
                .andExpect(jsonPath("$.entityId", org.hamcrest.Matchers.is("SKU1")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/audit/{id} returns 404 for unknown id")
    void getById_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/audit/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/audit/{id} returns entry when exists")
    void getById_returnsEntryWhenExists() throws Exception {
        AuditEntry e = auditService.record("DELETE", "Order", "O2", "user1");
        mockMvc.perform(get("/api/audit/" + e.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", org.hamcrest.Matchers.is(e.getId().intValue())))
                .andExpect(jsonPath("$.action", org.hamcrest.Matchers.is("DELETE")));
    }

    @Test
    @DisplayName("GET /api/audit?entityId= returns list for entity")
    void getByEntityId_returnsList() throws Exception {
        mockMvc.perform(get("/api/audit").param("entityId", "O1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("GET /api/audit/recent returns list")
    void listRecent_returnsList() throws Exception {
        mockMvc.perform(get("/api/audit/recent").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("POST /api/audit without userId uses system")
    void record_withoutUserId_usesSystem() throws Exception {
        mockMvc.perform(post("/api/audit")
                        .param("action", "VIEW")
                        .param("entityType", "Report")
                        .param("entityId", "R1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", org.hamcrest.Matchers.is("system")));
    }

    // Intentionally omit test for POST with missing required params -> 400 to leave one path uncovered for JaCoCo

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuditService auditService() {
            return new AuditService();
        }
    }
}
