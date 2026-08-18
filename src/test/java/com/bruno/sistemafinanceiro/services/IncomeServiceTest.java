package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.dto.requests.IncomeRequestDTO;
import com.bruno.sistemafinanceiro.dto.requests.RegisterUserRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.IncomeResponseDTO;
import com.bruno.sistemafinanceiro.entities.Income;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.entities.UserRole;
import com.bruno.sistemafinanceiro.repositories.IncomeRepository;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IncomeService incomeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return current income")
    void getCurrentCase1() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setName("Bruno");
        user.setRole(UserRole.USER);

        Income income = new Income();
        income.setUser(user);
        income.setAmount(new BigDecimal(3125));
        income.setStartDate(LocalDate.of(2026, 8, 1));

        when(incomeRepository.findIncomeByDateAndUserId(LocalDate.now(ZoneId.of("America/Sao_Paulo")), userId)).thenReturn(Optional.of(income));

        IncomeResponseDTO result = incomeService.getCurrent(userId);

        assertNotNull(result);
        assertEquals(income.getAmount(), result.amount());
        assertEquals(income.getStartDate(), result.startDate());
        assertEquals(income.getEndDate(), result.endDate());
    }

    @Test
    @DisplayName("Should not return current income")
    void getCurrentCase2() {
        UUID userId = UUID.randomUUID();

        when(incomeRepository.findIncomeByDateAndUserId(LocalDate.now(ZoneId.of("America/Sao_Paulo")), userId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> incomeService.getCurrent(userId));
        assertEquals("Current income not found", thrown.getMessage());
    }

    @Test
    @DisplayName("Should return all incomes")
    void getAllCase1() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setName("Bruno");
        user.setRole(UserRole.USER);

        Income income1 = new Income();
        income1.setUser(user);
        income1.setAmount(new BigDecimal(1700));
        income1.setStartDate(LocalDate.of(2024, 1, 1));
        income1.setEndDate(LocalDate.of(2024, 7, 14));

        Income income2 = new Income();
        income2.setUser(user);
        income2.setAmount(new BigDecimal(1455));
        income2.setStartDate(LocalDate.of(2024, 7, 15));
        income2.setEndDate(LocalDate.of(2026, 4, 12));

        Income income3 = new Income();
        income3.setUser(user);
        income3.setAmount(new BigDecimal(3125));
        income3.setStartDate(LocalDate.of(2026, 4, 13));

        when(incomeRepository.findAllByUserIdOrderByStartDateDesc(userId)).thenReturn(List.of(income1, income2, income3));

        List<IncomeResponseDTO> result = incomeService.getAll(userId);

        assertFalse(result.isEmpty());

        assertEquals(3, result.size());
        assertEquals(income1.getAmount(), result.get(0).amount());
        assertEquals(income2.getAmount(), result.get(1).amount());
        assertEquals(income3.getAmount(), result.get(2).amount());

        assertEquals(income1.getStartDate(), result.get(0).startDate());
        assertEquals(income2.getStartDate(), result.get(1).startDate());
        assertEquals(income3.getStartDate(), result.get(2).startDate());
    }

    @Test
    @DisplayName("Should return empty list")
    void getAllCase2() {
        UUID userId = UUID.randomUUID();

        when(incomeRepository.findAllByUserIdOrderByStartDateDesc(userId)).thenReturn(List.of());

        List<IncomeResponseDTO> result = incomeService.getAll(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should create the initial income")
    void createInitialIncomeCase1() {
        User user = new User();
        user.setName("Bruno");
        user.setRole(UserRole.USER);

        BigDecimal amount = new BigDecimal(3125);

        incomeService.createInitialIncome(amount, user);

        ArgumentCaptor<Income> captor = ArgumentCaptor.forClass(Income.class);

        verify(incomeRepository, times(1)).save(captor.capture());

        Income savedIncome = captor.getValue();

        assertEquals(user, savedIncome.getUser());
        assertEquals(amount, savedIncome.getAmount());
        assertEquals(LocalDate.now(ZoneId.of("America/Sao_Paulo")), savedIncome.getStartDate());
    }

    @Test
    @DisplayName("Should create income")
    void createCase1() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setName("Bruno");
        user.setRole(UserRole.USER);

        IncomeRequestDTO dto = new IncomeRequestDTO(new BigDecimal(3125), LocalDate.of(2026, 8, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(incomeRepository.findIncomeByDateAndUserId(dto.startDate(), userId)).thenReturn(Optional.empty());
        when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IncomeResponseDTO result = incomeService.create(dto, userId);

        assertNotNull(result);
        assertEquals(dto.amount(), result.amount());
        assertEquals(dto.startDate(), result.startDate());

        verify(incomeRepository, times(1)).save(any(Income.class));
    }

    @Test
    @DisplayName("Should close current income and create new income")
    void createCase2() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setName("Bruno");
        user.setRole(UserRole.USER);

        BigDecimal newAmount = new BigDecimal(4000);
        BigDecimal currentAmount = new BigDecimal(3125);

        IncomeRequestDTO dto = new IncomeRequestDTO(newAmount, LocalDate.of(2026, 8, 1));

        Income currentIncome = new Income();
        currentIncome.setUser(user);
        currentIncome.setAmount(currentAmount);
        currentIncome.setStartDate(LocalDate.of(2026, 1, 1));

        Income newIncome = new Income();
        newIncome.setUser(user);
        newIncome.setAmount(dto.amount());
        newIncome.setStartDate(dto.startDate());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(incomeRepository.findIncomeByDateAndUserId(dto.startDate(), userId)).thenReturn(Optional.of(currentIncome));

        incomeService.create(dto, userId);

        ArgumentCaptor<Income> captor = ArgumentCaptor.forClass(Income.class);

        verify(incomeRepository, times(2)).save(captor.capture());

        List<Income> savedIncomes = captor.getAllValues();

        assertEquals(dto.startDate().minusDays(1), currentIncome.getEndDate());
        assertEquals(currentAmount, savedIncomes.get(0).getAmount());
        assertEquals(newAmount, savedIncomes.get(1).getAmount());
    }

    @Test
    @DisplayName("Should not create income when user doesn't exist")
    void createCase3() {
        UUID userId = UUID.randomUUID();

        IncomeRequestDTO dto = new IncomeRequestDTO(new BigDecimal(4000), LocalDate.of(2026, 8, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> incomeService.create(dto, userId));

        assertEquals("User not found", thrown.getMessage());
        verify(incomeRepository, never()).save(any(Income.class));
    }
}