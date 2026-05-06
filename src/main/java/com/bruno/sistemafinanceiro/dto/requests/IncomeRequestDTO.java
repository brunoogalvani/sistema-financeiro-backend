package com.bruno.sistemafinanceiro.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeRequestDTO(

        @NotNull
        @Positive
        BigDecimal amount,

        @NotNull LocalDate startDate
) {
}
