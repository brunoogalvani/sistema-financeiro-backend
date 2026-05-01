package com.bruno.sistemafinanceiro.repositories;

import com.bruno.sistemafinanceiro.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByUserId(UUID userId);

    Optional<Category> findByIdAndUserId(UUID categoryId, UUID userId);

    boolean existsByNameIgnoreCaseAndUserId(String name, UUID userId);
}
