package com.bruno.sistemafinanceiro.dto.responses;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record InstallmentExpenseResponseDTO(
        UUID groupId,
        BigDecimal totalAmount,
        Integer totalInstallments,
        List<ExpenseResponseDTO> installments
) {
}
