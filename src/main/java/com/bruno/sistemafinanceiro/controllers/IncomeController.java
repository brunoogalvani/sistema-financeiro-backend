package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.commons.responses.ApiResponse;
import com.bruno.sistemafinanceiro.configs.JWTUserData;
import com.bruno.sistemafinanceiro.dto.requests.ExpenseRequestDTO;
import com.bruno.sistemafinanceiro.dto.requests.IncomeRequestDTO;
import com.bruno.sistemafinanceiro.dto.requests.IncomeUpdateRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.IncomeResponseDTO;
import com.bruno.sistemafinanceiro.services.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<IncomeResponseDTO>>> getCurrentIncome(@AuthenticationPrincipal JWTUserData user) {

        List<IncomeResponseDTO> currentIncomes = incomeService.getCurrent(user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Current income retrieved successfully", currentIncomes)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IncomeResponseDTO>>> getAllIncomes(@AuthenticationPrincipal JWTUserData user) {

        List<IncomeResponseDTO> allIncomes = incomeService.getAll(user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Incomes retrieved successfully", allIncomes)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponseDTO>> createNewIncome(
            @Valid @RequestBody IncomeRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        IncomeResponseDTO newIncome = incomeService.create(dto, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Income created successfully", newIncome)
        );
    }

    @PatchMapping("/{incomeId}")
    public ResponseEntity<ApiResponse<IncomeResponseDTO>> updateIncome(
            @PathVariable UUID incomeId,
            @RequestBody IncomeUpdateRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        IncomeResponseDTO updated = incomeService.update(incomeId, dto, user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Income updated successfully", updated)
        );
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<ApiResponse<Void>> deleteIncome(
            @PathVariable UUID incomeId,
            @AuthenticationPrincipal JWTUserData user
    ) {

        incomeService.delete(incomeId, user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Income deleted successfully", null)
        );
    }
}
