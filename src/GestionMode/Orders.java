package GestionMode;

import java.util.Date;

public class Orders {
    private int orderId;
    private Date orderDate;
    private String orderStatus;
    private String productDetails;
    private int totalWeight;
    private String OrderLocation;

    public Orders(int orderId, Date orderDate, String orderStatus, String productDetails, int totalWeight, String OrderLocation) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.productDetails = productDetails;
        this.totalWeight = totalWeight;
        this.OrderLocation = OrderLocation;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }
    
    public String getOrderLocation() {
        return OrderLocation;
    }

    public void setOrderLocation(String OrderLocation) {
        this.OrderLocation = OrderLocation;
    }
}
