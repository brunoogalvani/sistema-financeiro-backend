package com.bruno.sistemafinanceiro.dto.requests;

import com.bruno.sistemafinanceiro.entities.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegisterUserRequestDTO(
        @NotBlank String name,

        @NotNull
        @Positive
        BigDecimal initialIncome,

        UserRole role,

        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min = 6)
        String password
) {
}
