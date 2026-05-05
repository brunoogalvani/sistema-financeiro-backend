package com.bruno.sistemafinanceiro.repositories;

import com.bruno.sistemafinanceiro.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByUserId(UUID userId);

    @Query("""
        SELECT DISTINCT YEAR(e.date), MONTH(e.date)
        FROM Expense e
        WHERE e.user.id = :userId
        ORDER BY YEAR(e.date) DESC, MONTH(e.date) DESC
    """)
    List<Object[]> findAvailableMonths(UUID userId);

    Optional<Expense> findByIdAndUserId(UUID expenseId, UUID userId);

    boolean existsByCategoryId(UUID categoryId);
}
