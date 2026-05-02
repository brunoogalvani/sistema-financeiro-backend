package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.commons.responses.ApiResponse;
import com.bruno.sistemafinanceiro.configs.JWTUserData;
import com.bruno.sistemafinanceiro.dto.requests.UpdateUserRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.UserResponseDTO;
import com.bruno.sistemafinanceiro.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMe(@AuthenticationPrincipal JWTUserData user) {

        UserResponseDTO me = userService.findById(user.userId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User retrieved successfully", me)
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateMe(
            @Valid @RequestBody UpdateUserRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        UserResponseDTO updated = userService.updateCurrentUser(dto, user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User updated successfully", updated)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteMe(@AuthenticationPrincipal JWTUserData user) {

        userService.deleteCurrentUser(user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User deleted successfully", null)
        );
    }
}
