package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.BadRequestException;
import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.dto.requests.IncomeRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.IncomeResponseDTO;
import com.bruno.sistemafinanceiro.entities.Income;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.repositories.IncomeRepository;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeResponseDTO getCurrent(UUID userId) {

        Income currentIncome = incomeRepository.findIncomeByDateAndUserId(LocalDate.now(ZoneId.of("America/Sao_Paulo")), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Current income not found"));

        return toResponseDTO(currentIncome);
    }

    public List<IncomeResponseDTO> getAll(UUID userId) {

        List<Income> allIncomes = incomeRepository.findAllByUserIdOrderByStartDateDesc(userId);
        return allIncomes
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public void createInitialIncome(BigDecimal amount, User user) {
        Income income = new Income();
        income.setUser(user);
        income.setAmount(amount);
        income.setStartDate(LocalDate.now(ZoneId.of("America/Sao_Paulo")));

        incomeRepository.save(income);
    }

    @Transactional
    public IncomeResponseDTO create(IncomeRequestDTO dto, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Income currentIncome = incomeRepository
                .findIncomeByDateAndUserId(dto.startDate(), userId)
                .orElse(null);

        if (dto.startDate().isAfter(LocalDate.now(ZoneId.of("America/Sao_Paulo")).plusYears(1))) {
            throw new BadRequestException("Invalid start date");
        }

        if (currentIncome != null) {
            currentIncome.setEndDate(dto.startDate().minusDays(1));
            incomeRepository.save(currentIncome);
        }

        Income newIncome = new Income();
        newIncome.setUser(user);
        newIncome.setAmount(dto.amount());
        newIncome.setStartDate(dto.startDate());

        Income saved = incomeRepository.save(newIncome);

        return toResponseDTO(saved);
    }

    private IncomeResponseDTO toResponseDTO(Income income) {
        return new IncomeResponseDTO(income.getAmount(), income.getStartDate(), income.getEndDate());
    }
}
