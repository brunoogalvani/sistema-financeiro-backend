package com.bruno.sistemafinanceiro.services;

import com.bruno.sistemafinanceiro.entities.Category;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.repositories.CategoryRepository;
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

    public List<Category> findByUser(UUID userId) {
        return categoryRepository.findByUserId(userId);
    }

    public Category create(String name, UUID userId) {

        if (categoryRepository.existsByNameIgnoreCaseAndUserId(name.trim(), userId)) throw new RuntimeException("Category already exists");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = new Category();
        category.setName(name.trim());
        category.setUser(user);

        return categoryRepository.save(category);
    }
}
