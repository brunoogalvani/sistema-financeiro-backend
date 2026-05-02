package com.bruno.sistemafinanceiro.dto.requests;

import com.bruno.sistemafinanceiro.entities.UserRole;

import java.util.UUID;

public record AdminUpdateUserRequestDTO(
        UUID userId,
        String name,
        String username,
        UserRole role,
        String password
) {
}
