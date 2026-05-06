package com.bruno.sistemafinanceiro.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeResponseDTO(
        BigDecimal amount,
        LocalDate startDate,
        LocalDate endDate
) {
}
