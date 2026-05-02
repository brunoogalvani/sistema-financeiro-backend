package com.bruno.sistemafinanceiro.dto.requests;

public record UpdateUserRequestDTO(
        String name,
        String username,
        String password
) {
}
