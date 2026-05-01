package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.dto.requests.ExpenseRequestDTO;
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

    public ExpenseResponseDTO create(ExpenseRequestDTO dto, UUID userId) {

        User user = userRepository.getReferenceById(userId);

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

    public List<ExpenseResponseDTO> findByUser(UUID userId) {
        return expenseRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private ExpenseResponseDTO toResponseDTO(Expense expense) {

        String categoryName = expense.getCategory().getName();

        return new ExpenseResponseDTO(expense.getId(), expense.getName(), expense.getPrice(), categoryName, expense.getDate());
    }
}
