package com.bruno.sistemafinanceiro.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InstallmentExpenseRequestDTO(

        @NotBlank
        String name,

        @NotNull
        @Positive
        BigDecimal totalPrice,

        @NotNull
        UUID categoryId,

        @NotNull
        LocalDate firstDate,

        @NotNull
        @Min(1)
        Integer installments
) {
}
