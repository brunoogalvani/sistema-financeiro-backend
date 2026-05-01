package com.bruno.sistemafinanceiro.configs;

import com.bruno.sistemafinanceiro.entities.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record JWTUserData(UUID userId, String username, UserRole role) {
}
