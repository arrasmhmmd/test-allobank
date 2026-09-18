package com.allobank.splitbill.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Expense {
    private UUID id;
    private String description;
    private BigDecimal amount;
    private String paidBy;
    private List<String> beneficiaries;
    private Instant createdAt;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
    public List<String> getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(List<String> beneficiaries) { this.beneficiaries = beneficiaries; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
