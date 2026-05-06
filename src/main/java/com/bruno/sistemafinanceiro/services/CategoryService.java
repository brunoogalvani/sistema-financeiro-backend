package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.commons.exceptions.ConflictException;
import com.bruno.sistemafinanceiro.commons.exceptions.ResourceNotFoundException;
import com.bruno.sistemafinanceiro.entities.Category;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public List<Category> findByUser(UUID userId) {
        return categoryRepository.findByUserId(userId);
    }

    public void createDefaultCategories(User user) {

        List<String> defaultCategories = List.of(
                "Alimentação",
                "Transporte",
                "Moradia",
                "Lazer"
        );

        defaultCategories.forEach(name -> {
            Category c = new Category();
            c.setName(name);
            c.setUser(user);
            categoryRepository.save(c);
        });
    }

    public Category create(String name, UUID userId) {

        if (categoryRepository.existsByNameIgnoreCaseAndUserId(name.trim(), userId)) throw new ConflictException("Category already exists");

        User user = userRepository.getReferenceById(userId);

        Category category = new Category();
        category.setName(name.trim());
        category.setUser(user);

        return categoryRepository.save(category);
    }

    public Category update(UUID categoryId, String name, UUID userId) {

        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (
                !category.getName().equalsIgnoreCase(name.trim()) &&
                categoryRepository.existsByNameIgnoreCaseAndUserId(name.trim(), userId)
        ) throw new ConflictException("Category already exists");

        category.setName(name.trim());

        return categoryRepository.save(category);
    }

    public void delete(UUID categoryId, UUID userId) {

        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (expenseRepository.existsByCategoryId(categoryId)) throw new ConflictException("Category is being used");

        categoryRepository.delete(category);
    }
}
