package com.bruno.sistemafinanceiro.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(@NotBlank String name) {
}
