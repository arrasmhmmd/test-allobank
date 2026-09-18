package com.allobank.splitbill.utility;

import com.allobank.splitbill.model.BillGroup;
import com.allobank.splitbill.model.Expense;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class SettlementCalculatorTest {
    @Test
    void calculatesBalancesAndMinimalTransfersForEqualSplit() {
        BillGroup group = new BillGroup();
        group.setId(UUID.randomUUID()); group.setParticipants(List.of("Alice", "Bob", "Citra"));
        group.setExpenses(List.of(expense("Hotel", "90.00", "Alice"), expense("Dinner", "30.00", "Bob")));
        var result = SettlementCalculator.calculate(group, "johndoe47");
        assertThat(result.getTotalExpenses()).isEqualByComparingTo("120.00");
        assertThat(result.getBalances()).containsEntry("Alice", new BigDecimal("50.00")).containsEntry("Bob", new BigDecimal("-10.00")).containsEntry("Citra", new BigDecimal("-40.00"));
        assertThat(result.getTransfers()).hasSize(2);
    }
    private Expense expense(String description, String amount, String paidBy) {
        Expense expense = new Expense(); expense.setDescription(description); expense.setAmount(new BigDecimal(amount)); expense.setPaidBy(paidBy); expense.setBeneficiaries(List.of("Alice", "Bob", "Citra")); return expense;
    }
}
