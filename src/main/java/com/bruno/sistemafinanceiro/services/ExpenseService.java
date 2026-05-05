package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.dto.requests.ExpenseRequestDTO;
import com.bruno.sistemafinanceiro.dto.YearMonthDTO;
import com.bruno.sistemafinanceiro.dto.responses.ExpenseResponseDTO;
import com.bruno.sistemafinanceiro.entities.Category;
import com.bruno.sistemafinanceiro.entities.Expense;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.repositories.CategoryRepository;
import com.bruno.sistemafinanceiro.repositories.ExpenseRepository;
import com.bruno.sistemafinanceiro.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<YearMonthDTO> findAvailableMonths(UUID userId) {

        return expenseRepository.findAvailableMonths(userId)
                .stream()
                .map(obj -> new YearMonthDTO(
                        (Integer) obj[0],
                        (Integer) obj[1]
                ))
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

    public void delete(UUID expenseId, UUID userId) {

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        expenseRepository.delete(expense);
    }

    private ExpenseResponseDTO toResponseDTO(Expense expense) {

        String categoryName = expense.getCategory().getName();

        return new ExpenseResponseDTO(expense.getId(), expense.getName(), expense.getPrice(), categoryName, expense.getDate());
    }
}
