package com.vozhe.jwt.payload.request;

// PaymentDto.java

public class PaymentDto {
    private String paymentType; // e.g., "Cash"
    private String currency;
    private Double cost;

    // getters and setters
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}
