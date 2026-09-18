package com.allobank.splitbill.utility;

import com.allobank.splitbill.model.BillGroup;
import com.allobank.splitbill.model.Expense;
import com.allobank.splitbill.model.SettlementDTO;
import com.allobank.splitbill.model.Transfer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class SettlementCalculator {
    private static final BigDecimal ONE_CENT = new BigDecimal("0.01");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private SettlementCalculator() { }

    public static SettlementDTO calculate(BillGroup group, String githubUsername) {
        Map<String, BigDecimal> balances = createEmptyBalances(group);
        BigDecimal totalExpenses = BigDecimal.ZERO.setScale(2);

        // Tambahkan pembayaran, lalu kurangi bagian biaya setiap peserta.
        for (Expense expense : group.getExpenses()) {
            totalExpenses = totalExpenses.add(expense.getAmount());
            addPaidAmount(balances, expense);
            subtractEachPersonShare(balances, expense);
        }

        SettlementDTO settlement = new SettlementDTO();
        settlement.setGroupId(group.getId());
        settlement.setTotalExpenses(totalExpenses);
        settlement.setService_charge_pct(calculateServiceChargePercent(githubUsername));
        settlement.setService_charge_amount(calculateServiceChargeAmount(totalExpenses, settlement.getService_charge_pct()));
        settlement.setBalances(balances);
        settlement.setTransfers(createTransfers(balances));
        return settlement;
    }

    private static Map<String, BigDecimal> createEmptyBalances(BillGroup group) {
        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        for (String participant : group.getParticipants()) {
            balances.put(participant, BigDecimal.ZERO.setScale(2));
        }
        return balances;
    }

    private static void addPaidAmount(Map<String, BigDecimal> balances, Expense expense) {
        BigDecimal currentBalance = balances.get(expense.getPaidBy());
        balances.put(expense.getPaidBy(), currentBalance.add(expense.getAmount()));
    }

    private static void subtractEachPersonShare(Map<String, BigDecimal> balances, Expense expense) {
        List<BigDecimal> shares = splitEqually(expense.getAmount(), expense.getBeneficiaries().size());
        for (int index = 0; index < expense.getBeneficiaries().size(); index++) {
            String beneficiary = expense.getBeneficiaries().get(index);
            BigDecimal currentBalance = balances.get(beneficiary);
            balances.put(beneficiary, currentBalance.subtract(shares.get(index)));
        }
    }

    private static List<BigDecimal> splitEqually(BigDecimal amount, int numberOfPeople) {
        BigDecimal people = BigDecimal.valueOf(numberOfPeople);
        BigDecimal basicShare = amount.divide(people, 2, RoundingMode.DOWN);
        BigDecimal remainingAmount = amount.subtract(basicShare.multiply(people));
        List<BigDecimal> shares = new ArrayList<>();

        for (int index = 0; index < numberOfPeople; index++) {
            shares.add(basicShare);
        }

        // Sisa pembulatan satu sen diberikan mulai dari peserta pertama.
        int personIndex = 0;
        while (remainingAmount.signum() > 0) {
            shares.set(personIndex, shares.get(personIndex).add(ONE_CENT));
            remainingAmount = remainingAmount.subtract(ONE_CENT);
            personIndex++;
        }
        return shares;
    }

    private static int calculateServiceChargePercent(String githubUsername) {
        String username = githubUsername.toLowerCase(Locale.ROOT);
        int characterTotal = 0;
        for (int index = 0; index < username.length(); index++) {
            characterTotal = characterTotal + username.charAt(index);
        }
        return characterTotal % 10;
    }

    private static BigDecimal calculateServiceChargeAmount(BigDecimal totalExpenses, int percentage) {
        return totalExpenses.multiply(BigDecimal.valueOf(percentage)).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    private static List<Transfer> createTransfers(Map<String, BigDecimal> balances) {
        List<BalancePosition> debtors = new ArrayList<>();
        List<BalancePosition> creditors = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().signum() < 0) {
                debtors.add(new BalancePosition(entry.getKey(), entry.getValue().negate()));
            } else if (entry.getValue().signum() > 0) {
                creditors.add(new BalancePosition(entry.getKey(), entry.getValue()));
            }
        }

        List<Transfer> transfers = new ArrayList<>();
        int debtorIndex = 0;
        int creditorIndex = 0;

        // Pasangkan orang yang berutang dengan orang yang harus menerima uang.
        while (debtorIndex < debtors.size() && creditorIndex < creditors.size()) {
            BalancePosition debtor = debtors.get(debtorIndex);
            BalancePosition creditor = creditors.get(creditorIndex);
            BigDecimal transferAmount = debtor.amount.min(creditor.amount);

            transfers.add(new Transfer(debtor.person, creditor.person, transferAmount));
            debtor.amount = debtor.amount.subtract(transferAmount);
            creditor.amount = creditor.amount.subtract(transferAmount);

            if (debtor.amount.signum() == 0) debtorIndex++;
            if (creditor.amount.signum() == 0) creditorIndex++;
        }
        return transfers;
    }

    private static class BalancePosition {
        private String person;
        private BigDecimal amount;
        private BalancePosition(String person, BigDecimal amount) { this.person = person; this.amount = amount; }
    }
}
