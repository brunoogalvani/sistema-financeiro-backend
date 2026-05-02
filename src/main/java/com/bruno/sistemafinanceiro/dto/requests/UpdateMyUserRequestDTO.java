package com.bruno.sistemafinanceiro.dto.requests;

public record UpdateMyUserRequestDTO(
        String name,
        String username,
        String password
) {
}
