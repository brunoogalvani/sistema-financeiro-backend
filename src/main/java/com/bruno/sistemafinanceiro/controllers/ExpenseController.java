package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.dto.requests.ExpenseRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.ExpenseResponseDTO;
import com.bruno.sistemafinanceiro.services.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpenseByUser(@PathVariable UUID userId) {
        List<ExpenseResponseDTO> expenses = expenseService.findByUser(userId);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseRequestDTO dto) {
        ExpenseResponseDTO expense = expenseService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }
}
