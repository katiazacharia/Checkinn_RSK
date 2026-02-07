package com.project.checkinn.payment;

public class PaymentRequest {


    private String reference;
    private Double amount;
    private String method;
    private String status;

    public PaymentRequest() {}

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
