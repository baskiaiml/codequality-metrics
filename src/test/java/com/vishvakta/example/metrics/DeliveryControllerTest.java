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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryService.createDelivery("O1", "123 Main St");
    }

    @Test
    @DisplayName("POST /api/deliveries creates delivery and returns 201")
    void create_returns201AndBody() throws Exception {
        mockMvc.perform(post("/api/deliveries")
                        .param("orderId", "O2")
                        .param("address", "456 Oak Ave")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", org.hamcrest.Matchers.is("O2")))
                .andExpect(jsonPath("$.address", org.hamcrest.Matchers.is("456 Oak Ave")))
                .andExpect(jsonPath("$.status", org.hamcrest.Matchers.is("PENDING")));
    }

    @Test
    @DisplayName("GET /api/deliveries/{id} returns 404 for unknown id")
    void getById_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/deliveries/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/deliveries/{id} returns delivery when exists")
    void getById_returnsDeliveryWhenExists() throws Exception {
        Delivery d = deliveryService.createDelivery("O2", "456 Oak Ave");
        mockMvc.perform(get("/api/deliveries/" + d.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", org.hamcrest.Matchers.is(d.getId().intValue())))
                .andExpect(jsonPath("$.orderId", org.hamcrest.Matchers.is("O2")));
    }

    @Test
    @DisplayName("GET /api/deliveries?orderId= returns delivery when exists")
    void getByOrderId_returnsDeliveryWhenExists() throws Exception {
        mockMvc.perform(get("/api/deliveries").param("orderId", "O1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", org.hamcrest.Matchers.is("O1")));
    }

    @Test
    @DisplayName("GET /api/deliveries?orderId= returns 404 for unknown orderId")
    void getByOrderId_returns404ForUnknown() throws Exception {
        mockMvc.perform(get("/api/deliveries").param("orderId", "UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/deliveries/{id}/status updates status")
    void updateStatus_updatesStatus() throws Exception {
        Delivery d = deliveryService.getByOrderId("O1");
        mockMvc.perform(patch("/api/deliveries/" + d.getId() + "/status")
                        .param("status", "DISPATCHED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/deliveries/{id}/status returns 404 for unknown id")
    void updateStatus_returns404ForUnknownId() throws Exception {
        mockMvc.perform(patch("/api/deliveries/999/status")
                        .param("status", "DISPATCHED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/deliveries returns list of deliveries")
    void listAll_returnsList() throws Exception {
        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[?(@.orderId=='O1')].orderId", org.hamcrest.Matchers.hasItem("O1")));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DeliveryService deliveryService() {
            return new DeliveryService();
        }
    }
}
