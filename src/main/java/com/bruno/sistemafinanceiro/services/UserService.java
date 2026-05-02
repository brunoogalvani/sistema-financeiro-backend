package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.dto.requests.AdminUpdateUserRequestDTO;
import com.bruno.sistemafinanceiro.dto.requests.UpdateMyUserRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.UserResponseDTO;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(this::toResponseDTO)
                .toList();
    }

    public UserResponseDTO findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return toResponseDTO(user);
    }

    public UserResponseDTO adminUpdateUser(AdminUpdateUserRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.name() != null) user.setName(dto.name());
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.role() != null) user.setRole(dto.role());
        if (dto.password() != null) user.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(user);

        return toResponseDTO(user);
    }

    public UserResponseDTO updateCurrentUser(UpdateMyUserRequestDTO dto, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.name() != null) user.setName(dto.name());
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.password() != null) user.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(user);

        return toResponseDTO(user);
    }

    public void deleteUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setDeleted(true);
        userRepository.save(user);
    }

    private UserResponseDTO toResponseDTO(User dto) {
        return new UserResponseDTO(dto.getId(), dto.getName(), dto.getUsername(), dto.getRole());
    }
}
