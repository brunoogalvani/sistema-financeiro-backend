package com.bruno.sistemafinanceiro.entities;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    ADMIN("admin"),
    USER("user");

    @JsonValue
    private final String role;
}
