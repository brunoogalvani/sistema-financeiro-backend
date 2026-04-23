package com.bruno.sistemafinanceiro.configs;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String username) {
}
