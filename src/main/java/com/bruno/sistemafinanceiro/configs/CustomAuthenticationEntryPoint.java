package com.bruno.sistemafinanceiro.configs;

import com.bruno.sistemafinanceiro.commons.responses.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String message = request.getRequestURI().contains("/auth/login")
                ? "Invalid username or password"
                : "Token unavailable or invalid";

        ApiError error = new ApiError(
                false,
                "Unauthorized",
                message
        );

        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
