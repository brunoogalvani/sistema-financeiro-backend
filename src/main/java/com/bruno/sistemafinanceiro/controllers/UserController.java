package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.commons.responses.ApiResponse;
import com.bruno.sistemafinanceiro.dto.responses.UserResponseDTO;
import com.bruno.sistemafinanceiro.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsers() {
        List<UserResponseDTO> users = userService.findAll();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users retrieved successfully", users)
        );
    }
}
