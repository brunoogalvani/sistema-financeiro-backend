package com.bruno.sistemafinanceiro.dto.requests;

import jakarta.validation.constraints.Positive;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeUpdateRequestDTO(
        @Positive
        BigDecimal amount,
        LocalDate startDate,
        JsonNullable<LocalDate> endDate
) {
}
