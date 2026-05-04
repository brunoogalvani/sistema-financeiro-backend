package com.bruno.sistemafinanceiro.controllers;

import com.bruno.sistemafinanceiro.commons.responses.ApiResponse;
import com.bruno.sistemafinanceiro.configs.JWTUserData;
import com.bruno.sistemafinanceiro.dto.requests.CategoryRequestDTO;
import com.bruno.sistemafinanceiro.dto.responses.CategoryResponseDTO;
import com.bruno.sistemafinanceiro.entities.Category;
import com.bruno.sistemafinanceiro.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getCategories(@AuthenticationPrincipal JWTUserData user) {

        List<Category> categories = categoryService.findByUser(user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Categories retrieved successfully", categories.stream()
                        .map(c -> new CategoryResponseDTO(c.getId(), c.getName()))
                        .toList()
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CategoryRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        Category category = categoryService.create(dto.name(), user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Category created successfully", new CategoryResponseDTO(category.getId(), category.getName()))
        );
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequestDTO dto,
            @AuthenticationPrincipal JWTUserData user
    ) {

        Category category = categoryService.update(categoryId, dto.name(), user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category updated successfully", new CategoryResponseDTO(category.getId(), category.getName()))
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Category>> deleteCategory(
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal JWTUserData user
    ) {

        categoryService.delete(categoryId, user.userId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category deleted successfully", null)
        );
    }
}
