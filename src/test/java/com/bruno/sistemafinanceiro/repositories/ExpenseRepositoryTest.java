package com.bruno.sistemafinanceiro.repositories;

import com.bruno.sistemafinanceiro.dto.YearMonthDTO;
import com.bruno.sistemafinanceiro.entities.Expense;
import com.bruno.sistemafinanceiro.entities.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    @DisplayName("Should return expected amount of months with expenses")
    void findAvailableMonthsCase1() {
        User user = new User();
        user = userRepository.save(user);

        createExpense(user,"Despesa 1", new BigDecimal(150), 8, 1);
        createExpense(user,"Despesa 2", new BigDecimal(150), 9, 1);

        List<YearMonthDTO> result = expenseRepository.findAvailableMonths(user.getId());

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should not return months with expenses of another user")
    void findAvailableMonthsCase2() {
        User user1 = new User();
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2 = userRepository.save(user2);

        createExpense(user1,"Despesa 1", new BigDecimal(150), 8, 1);
        createExpense(user1,"Despesa 2", new BigDecimal(150), 9, 1);

        createExpense(user2,"Despesa 3", new BigDecimal(150), 10, 1);

        List<YearMonthDTO> result = expenseRepository.findAvailableMonths(user2.getId());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return expenses of a range of time")
    void findByMonthAndUserIdCase1() {
        User user = new User();
        user = userRepository.save(user);

        createExpense(user, "Despesa 1", new BigDecimal(150), 9, 12);
        createExpense(user, "Despesa 2", new BigDecimal(160), 10, 5);
        createExpense(user, "Despesa 3", new BigDecimal(170), 10, 20);

        List<Expense> result = expenseRepository.findByMonthAndUserId(LocalDate.of(2026, 10, 1),LocalDate.of(2026, 10, 31), user.getId());

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return expense on start day")
    void findByMonthAndUserIdCase2() {
        User user = new User();
        user = userRepository.save(user);

        createExpense(user, "Despesa Início", new BigDecimal(150), 10, 1);

        List<Expense> result = expenseRepository.findByMonthAndUserId(LocalDate.of(2026, 10, 1),LocalDate.of(2026, 10, 31), user.getId());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return expenses on end day")
    void findByMonthAndUserIdCase3() {
        User user = new User();
        user = userRepository.save(user);

        createExpense(user, "Despesa Fim", new BigDecimal(60), 10, 31);

        List<Expense> result = expenseRepository.findByMonthAndUserId(LocalDate.of(2026, 10, 1),LocalDate.of(2026, 10, 31), user.getId());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should not return past expense")
    void findByMonthAndUserIdCase4() {
        User user = new User();
        user = userRepository.save(user);

        createExpense(user, "Despesa Antes", new BigDecimal(150), 9, 30);

        List<Expense> result = expenseRepository.findByMonthAndUserId(LocalDate.of(2026, 10, 1),LocalDate.of(2026, 10, 31), user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return future expense")
    void findByMonthAndUserIdCase5() {
        User user = new User();
        user = userRepository.save(user);

        createExpense(user, "Despesa Depois", new BigDecimal(150), 11, 1);

        List<Expense> result = expenseRepository.findByMonthAndUserId(LocalDate.of(2026, 10, 1),LocalDate.of(2026, 10, 31), user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return expenses of a range of time of another user")
    void findByMonthAndUserIdCase6() {
        User user1 = new User();
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2 = userRepository.save(user2);

        createExpense(user1, "Despesa 1", new BigDecimal(160), 10, 5);
        createExpense(user1, "Despesa 2", new BigDecimal(150), 10, 12);
        createExpense(user2, "Despesa 3", new BigDecimal(170), 10, 20);

        List<Expense> result = expenseRepository.findByMonthAndUserId(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), user2.getId());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    private void createExpense(User usr, String name, BigDecimal amount, Integer month, Integer day) {
        Expense expense = new Expense();
        expense.setUser(usr);
        expense.setName(name);
        expense.setPrice(amount);
        expense.setDate(LocalDate.of(2026, month, day));
        expenseRepository.save(expense);
    }
}