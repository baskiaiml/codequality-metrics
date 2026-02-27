package com.vishvakta.example.metrics;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StockService {

    // Deliberate: unused constant for SpotBugs UUF_UNUSED_FIELD (quality metrics demo)
    private static final int DEFAULT_RESERVE_LIMIT = 1000;

    private final Map<String, Integer> quantityBySku = new ConcurrentHashMap<>();
    private final Map<String, Integer> reservedBySku = new ConcurrentHashMap<>();

    public Integer getQuantity(String sku) {
        if (sku == null) {
            return null;
        }
        Integer qty = quantityBySku.get(sku);
        Integer reserved = reservedBySku.getOrDefault(sku, 0);
        if (qty == null) {
            return null;
        }
        return qty - reserved;
    }

    public boolean addStock(String sku, int qty) {
        if (sku == null || qty < 0) {
            return false;
        }
        quantityBySku.merge(sku, qty, Integer::sum);
        return true;
    }

    public boolean reserve(String sku, int qty) {
        if (sku == null || qty < 0) {
            return false;
        }
        Integer available = getQuantity(sku);
        if (available == null || available < qty) {
            return false;
        }
        reservedBySku.merge(sku, qty, Integer::sum);
        return true;
    }

    public boolean release(String sku, int qty) {
        if (sku == null || qty < 0) {
            return false;
        }
        Integer reserved = reservedBySku.get(sku);
        if (reserved == null || reserved < qty) {
            return false;
        }
        if (reserved == qty) {
            reservedBySku.remove(sku);
        } else {
            reservedBySku.put(sku, reserved - qty);
        }
        return true;
    }

    public Map<String, Integer> getAll() {
        Map<String, Integer> result = new HashMap<>();
        for (String sku : quantityBySku.keySet()) {
            Integer available = getQuantity(sku);
            if (available != null) {
                result.put(sku, available);
            }
        }
        return Collections.unmodifiableMap(result);
    }
}
