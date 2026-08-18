package com.bruno.sistemafinanceiro.repositories;

import com.bruno.sistemafinanceiro.entities.Income;
import com.bruno.sistemafinanceiro.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should get actual income of user")
    void findIncomeByDateAndUserIdCase1() {
        User user = new User();
        user = userRepository.save(user);

        Income income = createIncome(user, new BigDecimal(2000), 7, 1);

        Optional<Income> result = incomeRepository.findIncomeByDateAndUserId(LocalDate.of(2026, 8, 15), user.getId());

        assertTrue(result.isPresent());
        assertEquals(income.getId(), result.get().getId());
        assertEquals(income.getAmount(), result.get().getAmount());
    }

    @Test
    @DisplayName("Should not get past income of user")
    void findIncomeByDateAndUserIdCase2() {
        User user = new User();
        user = userRepository.save(user);

        createIncome(user, new BigDecimal(2000), 5, 1, 8, 14);

        Optional<Income> result = incomeRepository.findIncomeByDateAndUserId(LocalDate.of(2026, 8, 15), user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not get future income of user")
    void findIncomeByDateAndUserIdCase3() {
        User user = new User();
        user = userRepository.save(user);

        createIncome(user, new BigDecimal(2000), 8, 30);

        Optional<Income> result = incomeRepository.findIncomeByDateAndUserId(LocalDate.of(2026, 8, 15), user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not get income of another user")
    void findIncomeByDateAndUserIdCase4() {
        User user1 = new User();
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2 = userRepository.save(user2);

        createIncome(user1, new BigDecimal(2000), 8, 1);

        Optional<Income> result = incomeRepository.findIncomeByDateAndUserId(LocalDate.of(2026, 8, 15), user2.getId());

        assertTrue(result.isEmpty());
    }

    private Income createIncome(User usr, BigDecimal amount, Integer startMonth, Integer startDay) {
        return createIncome(usr, amount, startMonth, startDay, null, null);
    }

    private Income createIncome(User usr, BigDecimal amount, Integer startMonth, Integer startDay, Integer endMonth, Integer endDay) {
        Income income = new Income();
        income.setUser(usr);
        income.setAmount(amount);
        income.setStartDate(LocalDate.of(2026, startMonth, startDay));

        if (endMonth != null &&  endDay != null) {
            income.setEndDate(LocalDate.of(2026, endMonth, endDay));
        }

        incomeRepository.save(income);

        return income;
    }
}