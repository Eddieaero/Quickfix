package com.aero.quickfix.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model class representing FIX trade/order data.
 */
public class TradeData implements Serializable {
    
    private String orderId;
    private String symbol;
    private String side; // BUY or SELL
    private Double quantity;
    private Double price;
    private String status; // NEW, PARTIALLY_FILLED, FILLED, CANCELLED, REJECTED
    private Long timestamp;
    private Double executedQty;
    private Double executedPrice;
    private String messageType;

    public TradeData() {}

    public TradeData(String orderId, String symbol, String side, Double quantity, 
                     Double price, String status, String messageType) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.messageType = messageType;
        this.timestamp = System.currentTimeMillis();
        this.executedQty = 0.0;
        this.executedPrice = 0.0;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(Double executedQty) {
        this.executedQty = executedQty;
    }

    public Double getExecutedPrice() {
        return executedPrice;
    }

    public void setExecutedPrice(Double executedPrice) {
        this.executedPrice = executedPrice;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "TradeData{" +
                "orderId='" + orderId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", side='" + side + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", executedQty=" + executedQty +
                ", executedPrice=" + executedPrice +
                ", timestamp=" + timestamp +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
