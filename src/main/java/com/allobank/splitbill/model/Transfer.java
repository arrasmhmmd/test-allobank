package com.allobank.splitbill.model;

import java.math.BigDecimal;

public class Transfer {
    private String from;
    private String to;
    private BigDecimal amount;
    public Transfer(String from, String to, BigDecimal amount) { this.from = from; this.to = to; this.amount = amount; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public BigDecimal getAmount() { return amount; }
}
