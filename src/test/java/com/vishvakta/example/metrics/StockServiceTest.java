package com.vishvakta.example.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StockServiceTest {

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService();
    }

    @Test
    @DisplayName("getQuantity returns null for unknown SKU")
    void getQuantity_returnsNullForUnknownSku() {
        assertNull(stockService.getQuantity("UNKNOWN"));
    }

    @Test
    @DisplayName("getQuantity returns null for null SKU")
    void getQuantity_returnsNullForNullSku() {
        assertNull(stockService.getQuantity(null));
    }

    @Test
    @DisplayName("addStock adds quantity and getQuantity returns it")
    void addStock_addsQuantity() {
        assertTrue(stockService.addStock("SKU1", 10));
        assertEquals(Integer.valueOf(10), stockService.getQuantity("SKU1"));
    }

    @Test
    @DisplayName("addStock merges quantity for same SKU")
    void addStock_mergesQuantity() {
        stockService.addStock("SKU1", 5);
        stockService.addStock("SKU1", 3);
        assertEquals(Integer.valueOf(8), stockService.getQuantity("SKU1"));
    }

    @Test
    @DisplayName("addStock returns false for null SKU")
    void addStock_returnsFalseForNullSku() {
        assertFalse(stockService.addStock(null, 10));
    }

    @Test
    @DisplayName("addStock returns false for negative quantity")
    void addStock_returnsFalseForNegativeQty() {
        assertFalse(stockService.addStock("SKU1", -1));
    }

    @Test
    @DisplayName("reserve reduces available quantity")
    void reserve_reducesAvailable() {
        stockService.addStock("SKU1", 10);
        assertTrue(stockService.reserve("SKU1", 3));
        assertEquals(Integer.valueOf(7), stockService.getQuantity("SKU1"));
    }

    @Test
    @DisplayName("reserve returns false when insufficient stock")
    void reserve_returnsFalseWhenInsufficient() {
        stockService.addStock("SKU1", 2);
        assertFalse(stockService.reserve("SKU1", 5));
    }

    @Test
    @DisplayName("reserve returns false for null SKU")
    void reserve_returnsFalseForNullSku() {
        assertFalse(stockService.reserve(null, 1));
    }

    @Test
    @DisplayName("reserve returns false for negative quantity")
    void reserve_returnsFalseForNegativeQty() {
        stockService.addStock("SKU1", 10);
        assertFalse(stockService.reserve("SKU1", -1));
    }

    @Test
    @DisplayName("release returns reserved quantity to available")
    void release_returnsToAvailable() {
        stockService.addStock("SKU1", 10);
        stockService.reserve("SKU1", 3);
        assertTrue(stockService.release("SKU1", 2));
        assertEquals(Integer.valueOf(9), stockService.getQuantity("SKU1"));
    }

    @Test
    @DisplayName("release returns false when insufficient reserved")
    void release_returnsFalseWhenInsufficientReserved() {
        stockService.addStock("SKU1", 10);
        stockService.reserve("SKU1", 2);
        assertFalse(stockService.release("SKU1", 5));
    }

    @Test
    @DisplayName("release returns false for unknown SKU")
    void release_returnsFalseForUnknownSku() {
        assertFalse(stockService.release("UNKNOWN", 1));
    }

    @Test
    @DisplayName("getAll returns empty map when no stock")
    void getAll_returnsEmptyWhenNone() {
        Map<String, Integer> all = stockService.getAll();
        assertTrue(all.isEmpty());
    }

    @Test
    @DisplayName("getAll returns all SKUs with available quantity")
    void getAll_returnsAllSkus() {
        stockService.addStock("A", 1);
        stockService.addStock("B", 2);
        Map<String, Integer> all = stockService.getAll();
        assertEquals(2, all.size());
        assertEquals(Integer.valueOf(1), all.get("A"));
        assertEquals(Integer.valueOf(2), all.get("B"));
    }
}
