package com.bruno.sistemafinanceiro.dto.requests;

import com.bruno.sistemafinanceiro.entities.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDTO(
        @NotBlank String name,
        @NotBlank UserRole role,

        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min = 6)
        String password
) {
}
