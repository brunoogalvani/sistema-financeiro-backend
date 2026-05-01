package com.bruno.sistemafinanceiro.dto.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequestDTO(String name, BigDecimal price, UUID categoryId, LocalDate date) {}
