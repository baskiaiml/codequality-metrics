package com.vishvakta.example.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceTest {

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryService = new DeliveryService();
    }

    @Test
    @DisplayName("createDelivery returns delivery with PENDING status")
    void createDelivery_returnsDeliveryWithPendingStatus() {
        Delivery d = deliveryService.createDelivery("O1", "123 Main St");
        assertNotNull(d.getId());
        assertEquals("O1", d.getOrderId());
        assertEquals("123 Main St", d.getAddress());
        assertEquals(DeliveryStatus.PENDING, d.getStatus());
    }

    @Test
    @DisplayName("createDelivery throws when orderId null")
    void createDelivery_throwsWhenOrderIdNull() {
        assertThrows(IllegalArgumentException.class,
                () -> deliveryService.createDelivery(null, "123 Main St"));
    }

    @Test
    @DisplayName("createDelivery throws when address null")
    void createDelivery_throwsWhenAddressNull() {
        assertThrows(IllegalArgumentException.class,
                () -> deliveryService.createDelivery("O1", null));
    }

    @Test
    @DisplayName("getDelivery returns null for unknown id")
    void getDelivery_returnsNullForUnknownId() {
        assertNull(deliveryService.getDelivery(999L));
    }

    @Test
    @DisplayName("getDelivery returns saved delivery")
    void getDelivery_returnsSavedDelivery() {
        Delivery created = deliveryService.createDelivery("O1", "123 Main St");
        Delivery found = deliveryService.getDelivery(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getByOrderId returns delivery when exists")
    void getByOrderId_returnsDeliveryWhenExists() {
        deliveryService.createDelivery("O1", "123 Main St");
        Delivery found = deliveryService.getByOrderId("O1");
        assertNotNull(found);
        assertEquals("O1", found.getOrderId());
    }

    @Test
    @DisplayName("getByOrderId returns null for unknown orderId")
    void getByOrderId_returnsNullForUnknown() {
        assertNull(deliveryService.getByOrderId("UNKNOWN"));
    }

    @Test
    @DisplayName("getByOrderId returns null for null orderId")
    void getByOrderId_returnsNullForNull() {
        assertNull(deliveryService.getByOrderId(null));
    }

    @Test
    @DisplayName("updateStatus updates delivery status")
    void updateStatus_updatesStatus() {
        Delivery d = deliveryService.createDelivery("O1", "123 Main St");
        assertTrue(deliveryService.updateStatus(d.getId(), DeliveryStatus.DISPATCHED));
        assertEquals(DeliveryStatus.DISPATCHED, deliveryService.getDelivery(d.getId()).getStatus());
    }

    @Test
    @DisplayName("updateStatus returns false for unknown id")
    void updateStatus_returnsFalseForUnknownId() {
        assertFalse(deliveryService.updateStatus(999L, DeliveryStatus.DISPATCHED));
    }

    @Test
    @DisplayName("listAll returns empty list when no deliveries")
    void listAll_returnsEmptyWhenNone() {
        List<Delivery> list = deliveryService.listAll();
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("listAll returns all deliveries")
    void listAll_returnsAllDeliveries() {
        deliveryService.createDelivery("O1", "Addr1");
        deliveryService.createDelivery("O2", "Addr2");
        assertEquals(2, deliveryService.listAll().size());
    }
}
