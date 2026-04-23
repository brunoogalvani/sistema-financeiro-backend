package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.dto.requests.RegisterUserRequest;
import com.bruno.sistemafinanceiro.dto.responses.RegisterUserResponse;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.entities.UserRole;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserResponse create(RegisterUserRequest request) {

        User user = new User();
        user.setName(request.name());
        user.setRole(request.role() != null ? request.role() : UserRole.USER);
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);

        return toRegisterResponseDTO(saved);
    }

    private RegisterUserResponse toRegisterResponseDTO(User dto) {
        return new RegisterUserResponse(dto.getId(), dto.getName(), dto.getUsername(), dto.getRole());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
