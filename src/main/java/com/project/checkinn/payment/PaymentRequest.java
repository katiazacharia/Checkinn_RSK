package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;

import java.math.BigDecimal;

public class PaymentRequest {


    private String reference;
    private BigDecimal amount;

    private String status;
    private PaymentMethod method;

    public PaymentRequest() {}

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
