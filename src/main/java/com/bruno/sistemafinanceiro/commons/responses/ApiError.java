package com.bruno.sistemafinanceiro.commons.responses;

public record ApiError(
        boolean success,
        String error,
        String message
) {
}
