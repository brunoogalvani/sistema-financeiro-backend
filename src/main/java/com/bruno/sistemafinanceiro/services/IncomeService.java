package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.BadRequestException;
import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.dto.requests.IncomeRequestDTO;
import com.bruno.sistemafinanceiro.dto.requests.IncomeUpdateRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.IncomeResponseDTO;
import com.bruno.sistemafinanceiro.entities.Income;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.repositories.IncomeRepository;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
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

    public List<IncomeResponseDTO> getCurrent(UUID userId) {

        List<Income> currentIncomes = incomeRepository.findIncomeByDateAndUserId(LocalDate.now(ZoneId.of("America/Sao_Paulo")), userId);

        return currentIncomes
                .stream()
                .map(this::toResponseDTO)
                .toList();
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

    public IncomeResponseDTO create(IncomeRequestDTO dto, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Income newIncome = new Income();
        newIncome.setUser(user);
        newIncome.setAmount(dto.amount());
        newIncome.setStartDate(dto.startDate());

        Income saved = incomeRepository.save(newIncome);

        return toResponseDTO(saved);
    }

    public IncomeResponseDTO update(UUID incomeId, IncomeUpdateRequestDTO dto, UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Income income = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found"));

        if (dto.amount() != null) income.setAmount(dto.amount());
        if (dto.startDate() != null) income.setStartDate(dto.startDate());
        if (dto.endDate().isPresent()) income.setEndDate(dto.endDate().get());

        if (income.getEndDate() != null &&
                income.getStartDate().isAfter(income.getEndDate())
        ) throw new BadRequestException("Start date cannot be after end date");

        incomeRepository.save(income);

        return toResponseDTO(income);
    }

    public void delete(UUID incomeId, UUID userId) {

        Income income = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found"));

        incomeRepository.delete(income);
    }

    private IncomeResponseDTO toResponseDTO(Income income) {
        return new IncomeResponseDTO(income.getAmount(), income.getStartDate(), income.getEndDate());
    }
}
