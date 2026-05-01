package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.commons.responses.ApiResponse;
import com.bruno.sistemafinanceiro.configs.TokenConfig;
import com.bruno.sistemafinanceiro.dto.requests.LoginRequest;
import com.bruno.sistemafinanceiro.dto.requests.RegisterUserRequest;
import com.bruno.sistemafinanceiro.dto.responses.LoginResponse;
import com.bruno.sistemafinanceiro.dto.responses.RegisterUserResponse;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        LoginResponse token = new LoginResponse(tokenConfig.generateToken(user));
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Authentication successful", token)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserResponse created = authService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "User registered successfully", created)
        );
    }
}
