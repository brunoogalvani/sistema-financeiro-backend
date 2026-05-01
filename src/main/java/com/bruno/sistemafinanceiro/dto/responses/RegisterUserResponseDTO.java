package com.bruno.sistemafinanceiro.dto.responses;

import com.bruno.sistemafinanceiro.entities.UserRole;

import java.util.UUID;

public record RegisterUserResponseDTO(UUID id, String name, String username, UserRole role) {
}
