package com.vishvakta.example.metrics;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeliveryService {

    private final Map<Long, Delivery> deliveries = new ConcurrentHashMap<>();
    private long nextId = 1L;

    public Delivery createDelivery(String orderId, String address) {
        if (orderId == null || address == null) {
            throw new IllegalArgumentException("orderId and address must not be null");
        }
        Delivery d = new Delivery(nextId++, orderId, address, DeliveryStatus.PENDING);
        deliveries.put(d.getId(), d);
        return d;
    }

    public Delivery getDelivery(Long id) {
        return deliveries.get(id);
    }

    public Delivery getByOrderId(String orderId) {
        if (orderId == null) {
            return null;
        }
        for (Delivery d : deliveries.values()) {
            if (orderId.equals(d.getOrderId())) {
                return d;
            }
        }
        return null;
    }

    public boolean updateStatus(Long id, DeliveryStatus status) {
        if (id == null || status == null) {
            return false;
        }
        Delivery d = deliveries.get(id);
        if (d == null) {
            return false;
        }
        d.setStatus(status);
        return true;
    }

    public List<Delivery> listAll() {
        return new ArrayList<>(deliveries.values());
    }
}
