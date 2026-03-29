package com.wms.models;

import java.sql.Timestamp;

public class Shipment {
    private int id;
    private int orderId;
    private String status;
    private String trackingNumber;
    private Timestamp shipDate;

    public Shipment() {}

    public Shipment(int id, int orderId, String status, String trackingNumber, Timestamp shipDate) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.trackingNumber = trackingNumber;
        this.shipDate = shipDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public Timestamp getShipDate() { return shipDate; }
    public void setShipDate(Timestamp shipDate) { this.shipDate = shipDate; }
}
