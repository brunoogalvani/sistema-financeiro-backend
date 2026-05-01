package com.bruno.sistemafinanceiro.dto.requests;

import com.bruno.sistemafinanceiro.entities.UserRole;

public record RegisterUserRequestDTO(String name, UserRole role, String username, String password) {
}
