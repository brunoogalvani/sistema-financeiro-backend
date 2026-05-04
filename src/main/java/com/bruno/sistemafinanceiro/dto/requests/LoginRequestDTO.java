package com.bruno.sistemafinanceiro.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank String username,
        @NotBlank String password
) {
}
