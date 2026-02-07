package com.project.checkinn.payment;

import java.time.Instant;

public class PaymentResponse {


        private Long id;
        private String reference;
        private Double amount;
        private String method;
        private String status;
        private Instant createdAt;

        public PaymentResponse() {}

        public Long getId() {

            return id;

        }
        public void setId(Long id) {

            this.id = id;

        }

        public String getReference() {

            return reference;

        }
        public void setReference(String reference) {

            this.reference = reference;

        }

        public Double getAmount() {

            return amount;

        }
        public void setAmount(Double amount) {

            this.amount = amount;

        }

        public String getMethod() {

            return method;

        }
        public void setMethod(String method) {

            this.method = method;

        }

        public String getStatus() {

            return status;
        }
        public void setStatus(String status) {

            this.status = status;

        }

        public Instant getCreatedAt() {

            return createdAt;
        }


        public void setCreatedAt(Instant createdAt) {

            this.createdAt = createdAt;

        }
    }



