package com.bruno.sistemafinanceiro.dto.responses;

import java.util.UUID;

public record CategoryResponseDTO(
        UUID id,
        String name
) {
}
