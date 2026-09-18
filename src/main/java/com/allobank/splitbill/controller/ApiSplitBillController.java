package com.allobank.splitbill.controller;

import com.allobank.splitbill.model.AddExpenseDTO;
import com.allobank.splitbill.model.ApiResponse;
import com.allobank.splitbill.model.BillGroup;
import com.allobank.splitbill.model.CreateGroupDTO;
import com.allobank.splitbill.model.Expense;
import com.allobank.splitbill.model.SettlementDTO;
import com.allobank.splitbill.repository.BillGroupRepository;
import com.allobank.splitbill.utility.ResponseUtil;
import com.allobank.splitbill.utility.SettlementCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/split-bill")
public class ApiSplitBillController {
    private static final Logger logger = LoggerFactory.getLogger(ApiSplitBillController.class);

    @Autowired
    private BillGroupRepository billGroupRepository;

    @Value("${app.github-username}")
    private String githubUsername;

    @PostMapping("/groups")
    public ResponseEntity<ApiResponse<BillGroup>> createGroup(@RequestBody CreateGroupDTO request) {
        String errorMessage = validateCreateGroupRequest(request);
        if (errorMessage != null) {
            return ResponseUtil.generateErrorResponse("Validation error", errorMessage, HttpStatus.BAD_REQUEST);
        }

        BillGroup group = new BillGroup();
        group.setId(UUID.randomUUID());
        group.setName(request.getName().trim());
        group.setParticipants(cleanNames(request.getParticipants()));
        group.setCreatedAt(Instant.now());

        billGroupRepository.save(group);
        logger.info("Group created: {}", group.getId());
        return ResponseUtil.generateSuccessResponse("Group created successfully", group, HttpStatus.CREATED);
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<ApiResponse<BillGroup>> getGroup(@PathVariable UUID groupId) {
        Optional<BillGroup> group = billGroupRepository.findById(groupId);
        if (group.isEmpty()) {
            return ResponseUtil.generateErrorResponse("Not found", "Bill group not found", HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.generateSuccessResponse("Group found", group.get(), HttpStatus.OK);
    }

    @PostMapping("/groups/{groupId}/expenses")
    public ResponseEntity<ApiResponse<Expense>> addExpense(@PathVariable UUID groupId, @RequestBody AddExpenseDTO request) {
        Optional<BillGroup> groupResult = billGroupRepository.findById(groupId);
        if (groupResult.isEmpty()) {
            return ResponseUtil.generateErrorResponse("Not found", "Bill group not found", HttpStatus.NOT_FOUND);
        }

        BillGroup group = groupResult.get();
        String errorMessage = validateExpenseRequest(group, request);
        if (errorMessage != null) {
            return ResponseUtil.generateErrorResponse("Validation error", errorMessage, HttpStatus.BAD_REQUEST);
        }

        Expense expense = new Expense();
        expense.setId(UUID.randomUUID());
        expense.setDescription(request.getDescription().trim());
        expense.setAmount(request.getAmount().setScale(2, RoundingMode.HALF_UP));
        expense.setPaidBy(request.getPaidBy().trim());
        expense.setBeneficiaries(cleanNames(request.getBeneficiaries()));
        expense.setCreatedAt(Instant.now());

        // Satu group hanya ditambah oleh satu request pada satu waktu.
        synchronized (group) {
            group.getExpenses().add(expense);
        }

        logger.info("Expense added to group: {}", groupId);
        return ResponseUtil.generateSuccessResponse("Expense added successfully", expense, HttpStatus.CREATED);
    }

    @GetMapping("/groups/{groupId}/settlement")
    public ResponseEntity<ApiResponse<SettlementDTO>> getSettlement(@PathVariable UUID groupId) {
        Optional<BillGroup> group = billGroupRepository.findById(groupId);
        if (group.isEmpty()) {
            return ResponseUtil.generateErrorResponse("Not found", "Bill group not found", HttpStatus.NOT_FOUND);
        }

        SettlementDTO settlement = SettlementCalculator.calculate(group.get(), githubUsername);
        return ResponseUtil.generateSuccessResponse("Settlement calculated successfully", settlement, HttpStatus.OK);
    }

    private String validateCreateGroupRequest(CreateGroupDTO request) {
        if (request == null || isBlank(request.getName())) return "Group name is required";
        if (request.getParticipants() == null || request.getParticipants().isEmpty()) return "At least one participant is required";

        List<String> participants = cleanNames(request.getParticipants());
        if (participants.size() != request.getParticipants().size() || participants.contains("")) return "Every participant name is required";
        if (new HashSet<>(participants).size() != participants.size()) return "Participant names must be unique";
        return null;
    }

    private String validateExpenseRequest(BillGroup group, AddExpenseDTO request) {
        if (request == null || isBlank(request.getDescription())) return "Description is required";
        if (request.getAmount() == null || request.getAmount().signum() <= 0) return "Amount must be greater than zero";
        if (isBlank(request.getPaidBy())) return "Payer is required";
        if (!group.getParticipants().contains(request.getPaidBy().trim())) return "Payer must be a group participant";
        if (request.getBeneficiaries() == null || request.getBeneficiaries().isEmpty()) return "At least one beneficiary is required";

        List<String> beneficiaries = cleanNames(request.getBeneficiaries());
        if (beneficiaries.size() != request.getBeneficiaries().size() || beneficiaries.contains("")) return "Every beneficiary name is required";
        if (new HashSet<>(beneficiaries).size() != beneficiaries.size()) return "Beneficiaries must be unique";
        if (!group.getParticipants().containsAll(beneficiaries)) return "Every beneficiary must be a group participant";
        return null;
    }

    private List<String> cleanNames(List<String> names) {
        List<String> cleanedNames = new ArrayList<>();
        for (String name : names) {
            cleanedNames.add(name == null ? "" : name.trim());
        }
        return cleanedNames;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
