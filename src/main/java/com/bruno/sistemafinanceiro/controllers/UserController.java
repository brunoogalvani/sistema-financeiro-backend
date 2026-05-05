package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.commons.responses.ApiResponse;
import com.bruno.sistemafinanceiro.configs.JWTUserData;
import com.bruno.sistemafinanceiro.dto.requests.AdminUpdateUserRequestDTO;
import com.bruno.sistemafinanceiro.dto.requests.UpdateMyUserRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.UserResponseDTO;
import com.bruno.sistemafinanceiro.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@RequestBody AdminUpdateUserRequestDTO dto) {

        UserResponseDTO updated = userService.adminUpdateUser(dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User updated successfully", updated)
        );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {

        userService.deleteUser(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User deleted successfully", null)
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
            @RequestBody UpdateMyUserRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        UserResponseDTO updated = userService.updateCurrentUser(dto, user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User updated successfully", updated)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMe(@AuthenticationPrincipal JWTUserData user) {

        userService.deleteUser(user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User deleted successfully", null)
        );
    }
}
