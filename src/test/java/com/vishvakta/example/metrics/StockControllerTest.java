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

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService.addStock("SKU1", 10);
    }

    @Test
    @DisplayName("GET /api/stock/{sku} returns 404 for unknown SKU")
    void getQuantity_returns404ForUnknownSku() throws Exception {
        mockMvc.perform(get("/api/stock/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/stock/{sku} returns quantity when exists")
    void getQuantity_returnsQuantityWhenExists() throws Exception {
        mockMvc.perform(get("/api/stock/SKU1"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    @DisplayName("PUT /api/stock/{sku} adds stock")
    void addStock_addsStock() throws Exception {
        mockMvc.perform(put("/api/stock/SKU2").param("quantity", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/stock/SKU2"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("POST /api/stock/{sku}/reserve reserves quantity")
    void reserve_reservesQuantity() throws Exception {
        String sku = "SKU_RESERVE";
        mockMvc.perform(put("/api/stock/" + sku).param("quantity", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/stock/" + sku + "/reserve").param("quantity", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        mockMvc.perform(get("/api/stock/" + sku))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    @DisplayName("POST /api/stock/{sku}/release releases quantity")
    void release_releasesQuantity() throws Exception {
        String sku = "SKU_RELEASE";
        mockMvc.perform(put("/api/stock/" + sku).param("quantity", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/stock/" + sku + "/reserve").param("quantity", "4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        mockMvc.perform(post("/api/stock/" + sku + "/release").param("quantity", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        mockMvc.perform(get("/api/stock/" + sku))
                .andExpect(status().isOk())
                .andExpect(content().string("8"));
    }

    @Test
    @DisplayName("GET /api/stock returns all SKUs")
    void listAll_returnsAllSkus() throws Exception {
        mockMvc.perform(get("/api/stock"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$", hasKey("SKU1")));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public StockService stockService() {
            return new StockService();
        }
    }
}
