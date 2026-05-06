package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.dto.requests.RegisterUserRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.RegisterUserResponseDTO;
import com.bruno.sistemafinanceiro.entities.Category;
import com.bruno.sistemafinanceiro.entities.Income;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.entities.UserRole;
import com.bruno.sistemafinanceiro.repositories.CategoryRepository;
import com.bruno.sistemafinanceiro.repositories.IncomeRepository;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final IncomeService incomeService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterUserResponseDTO create(RegisterUserRequestDTO request) {

        User user = new User();
        user.setName(request.name());
        user.setRole(request.role() != null ? request.role() : UserRole.USER);
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);

        categoryService.createDefaultCategories(saved);
        incomeService.createInitialIncome(request.initialIncome(), saved);

        return toRegisterResponseDTO(saved);
    }

    private RegisterUserResponseDTO toRegisterResponseDTO(User dto) {
        return new RegisterUserResponseDTO(dto.getId(), dto.getName(), dto.getUsername(), dto.getRole());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
