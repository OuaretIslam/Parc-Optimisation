package GestionMode;

import java.util.Date;

public class Deliveries {
    private int deliveryId;
    private int orderId;
    private int truckId;
    private Date DeliveryD;
    private String deliveryStatus;

    // Constructor
    public Deliveries(int deliveryId, int orderId, int truckId, Date DeliveryD, String deliveryStatus) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.truckId = truckId;
        this.DeliveryD = DeliveryD;
        this.deliveryStatus = deliveryStatus;
    }

    // Getters and Setters
    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getTruckId() {
        return truckId;
    }

    public void setTruckId(int truckId) {
        this.truckId = truckId;
    }

    public Date getDeliveryD() {
        return DeliveryD;
    }

    public void setDeliveryD(Date DeliveryD) {
        this.DeliveryD = DeliveryD;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
