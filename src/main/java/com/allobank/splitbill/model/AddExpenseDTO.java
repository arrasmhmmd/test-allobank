package com.allobank.splitbill.model;

import java.math.BigDecimal;
import java.util.List;

public class AddExpenseDTO {
    private String description;
    private BigDecimal amount;
    private String paidBy;
    private List<String> beneficiaries;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
    public List<String> getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(List<String> beneficiaries) { this.beneficiaries = beneficiaries; }
}
