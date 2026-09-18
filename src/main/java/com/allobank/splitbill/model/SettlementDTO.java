package com.allobank.splitbill.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SettlementDTO {
    private UUID groupId;
    private BigDecimal totalExpenses;
    private int service_charge_pct;
    private BigDecimal service_charge_amount;
    private Map<String, BigDecimal> balances;
    private List<Transfer> transfers;
    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    public int getService_charge_pct() { return service_charge_pct; }
    public void setService_charge_pct(int serviceChargePct) { this.service_charge_pct = serviceChargePct; }
    public BigDecimal getService_charge_amount() { return service_charge_amount; }
    public void setService_charge_amount(BigDecimal serviceChargeAmount) { this.service_charge_amount = serviceChargeAmount; }
    public Map<String, BigDecimal> getBalances() { return balances; }
    public void setBalances(Map<String, BigDecimal> balances) { this.balances = balances; }
    public List<Transfer> getTransfers() { return transfers; }
    public void setTransfers(List<Transfer> transfers) { this.transfers = transfers; }
}
