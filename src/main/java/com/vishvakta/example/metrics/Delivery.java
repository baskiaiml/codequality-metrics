package com.vishvakta.example.metrics;

import java.util.Objects;

public class Delivery {

    private final Long id;
    private final String orderId;
    private final String address;
    private DeliveryStatus status;

    public Delivery(Long id, String orderId, String address, DeliveryStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.address = address;
        this.status = status != null ? status : DeliveryStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAddress() {
        return address;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Delivery delivery = (Delivery) o;
        return Objects.equals(id, delivery.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
