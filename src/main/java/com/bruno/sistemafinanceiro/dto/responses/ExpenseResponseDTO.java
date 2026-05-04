package com.bruno.sistemafinanceiro.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponseDTO(
        UUID id,
        String name,
        BigDecimal price,
        String categoryName,
        LocalDate date
) {
}
