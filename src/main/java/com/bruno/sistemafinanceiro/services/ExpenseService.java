package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.BadRequestException;
import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.dto.requests.ExpenseRequestDTO;
import com.bruno.sistemafinanceiro.dto.YearMonthDTO;
import com.bruno.sistemafinanceiro.dto.requests.InstallmentExpenseRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.ExpenseResponseDTO;
import com.bruno.sistemafinanceiro.dto.responses.InstallmentExpenseResponseDTO;
import com.bruno.sistemafinanceiro.entities.Category;
import com.bruno.sistemafinanceiro.entities.Expense;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.repositories.CategoryRepository;
import com.bruno.sistemafinanceiro.repositories.ExpenseRepository;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<ExpenseResponseDTO> findByUser(UUID userId) {
        return expenseRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<String> findAvailableMonths(UUID userId) {

        return expenseRepository.findAvailableMonths(userId)
                .stream()
                .map(m -> m.year() + "-" + String.format("%02d", m.month()))
                .toList();
    }

    public List<ExpenseResponseDTO> findByMonthAndUser(String month, UUID userId) {

        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(month);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid month format. Use YYYY-MM");
        }

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        return expenseRepository.findByMonthAndUserId(start, end, userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ExpenseResponseDTO create(ExpenseRequestDTO dto, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findByIdAndUserId(dto.categoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Expense expense = new Expense();
        expense.setName(dto.name());
        expense.setPrice(dto.price());
        expense.setUser(user);
        expense.setCategory(category);
        expense.setDate(dto.date());

        Expense saved = expenseRepository.save(expense);

        return toResponseDTO(saved);
    }

    public InstallmentExpenseResponseDTO createInstallments(InstallmentExpenseRequestDTO dto, UUID userId) {

        User user = userRepository.getReferenceById(userId);

        Category category = categoryRepository.findByIdAndUserId(dto.categoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        BigDecimal total = dto.totalPrice();
        int installments = dto.installments();

        BigDecimal baseValue = total
                .divide(BigDecimal.valueOf(installments), 2, RoundingMode.DOWN);

        BigDecimal accumulated = BigDecimal.ZERO;

        UUID groupId = UUID.randomUUID();

        List<Expense> expenses = new ArrayList<>();

        for (int i = 0; i < installments; i++) {

            Expense expense = new Expense();
            expense.setName(dto.name());
            expense.setUser(user);
            expense.setCategory(category);
            expense.setDate(dto.firstDate().plusMonths(i));
            expense.setInstallmentGroupId(groupId);
            expense.setTotalInstallments(installments);
            expense.setInstallmentNumber(i + 1);

            if (i == installments - 1) {
                expense.setPrice(total.subtract(accumulated));
            } else {
                expense.setPrice(baseValue);
                accumulated = accumulated.add(baseValue);
            }

            expenses.add(expense);
        }

        expenseRepository.saveAll(expenses);

        List<ExpenseResponseDTO> responseList = expenses.stream()
                .map(this::toResponseDTO)
                .toList();

        return new InstallmentExpenseResponseDTO(
                groupId,
                total,
                installments,
                responseList
        );
    }

    public ExpenseResponseDTO update(UUID expenseId, ExpenseRequestDTO dto, UUID userId) {

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (dto.name() != null) expense.setName(dto.name());
        if (dto.price() != null) expense.setPrice(dto.price());
        if (dto.categoryId() != null) {
            Category category = categoryRepository.findByIdAndUserId(dto.categoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            expense.setCategory(category);
        }
        if(dto.date() != null) expense.setDate(dto.date());

        Expense updated = expenseRepository.save(expense);

        return toResponseDTO(updated);
    }

    public InstallmentExpenseResponseDTO updateInstallments(UUID groupId, InstallmentExpenseRequestDTO dto, UUID userId) {

        deleteInstallmentGroup(groupId, userId);
        return createInstallments(dto, userId);
    }

    public void delete(UUID expenseId, UUID userId) {

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        expenseRepository.delete(expense);
    }

    public void deleteInstallmentGroup(UUID groupId, UUID userId) {

        List<Expense> expenses = expenseRepository.findByInstallmentGroupIdAndUserId(groupId, userId);

        if (expenses.isEmpty()) throw new ResourceNotFoundException("Installments not found");

        expenseRepository.deleteAll(expenses);
    }

    private ExpenseResponseDTO toResponseDTO(Expense expense) {

        String categoryName = expense.getCategory().getName();

        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getName(),
                expense.getPrice(),
                categoryName,
                expense.getDate(),
                expense.getInstallmentGroupId(),
                expense.getInstallmentNumber(),
                expense.getTotalInstallments()
        );
    }
}
