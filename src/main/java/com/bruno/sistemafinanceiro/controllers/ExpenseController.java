package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.commons.responses.ApiResponse;
import com.bruno.sistemafinanceiro.configs.JWTUserData;
import com.bruno.sistemafinanceiro.dto.requests.ExpenseRequestDTO;
import com.bruno.sistemafinanceiro.dto.YearMonthDTO;
import com.bruno.sistemafinanceiro.dto.responses.ExpenseResponseDTO;
import com.bruno.sistemafinanceiro.services.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponseDTO>>> getExpenses(@AuthenticationPrincipal JWTUserData user) {

        List<ExpenseResponseDTO> expenses = expenseService.findByUser(user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Expenses retrieved successfully", expenses)
        );
    }

    @GetMapping("/months")
    public ResponseEntity<ApiResponse<List<YearMonthDTO>>> getAvailableMonths(@AuthenticationPrincipal JWTUserData user) {

        List<YearMonthDTO> months = expenseService.findAvailableMonths(user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Months retrieved successfully", months)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> createExpense(
            @RequestBody ExpenseRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        ExpenseResponseDTO expense = expenseService.create(dto, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Expense created successfully", expense)
        );
    }

    @PatchMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> updateExpense(
            @PathVariable UUID expenseId,
            @RequestBody ExpenseRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        ExpenseResponseDTO updated = expenseService.update(expenseId, dto, user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Expense updated successfully", updated)
        );
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> deleteExpense(
            @PathVariable UUID expenseId,
            @AuthenticationPrincipal JWTUserData user
    ) {

        expenseService.delete(expenseId, user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Expense deleted successfully", null)
        );
    }
}
