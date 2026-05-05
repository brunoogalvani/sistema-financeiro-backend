package com.bruno.sistemafinanceiro.repositories;

import com.bruno.sistemafinanceiro.dto.YearMonthDTO;
import com.bruno.sistemafinanceiro.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByUserId(UUID userId);

    @Query("""
        SELECT new com.bruno.sistemafinanceiro.dto.YearMonthDTO(
            YEAR(e.date),
            MONTH(e.date)
        )
        FROM Expense e
        WHERE e.user.id = :userId
        GROUP BY YEAR(e.date), MONTH(e.date)
        ORDER BY YEAR(e.date) DESC, MONTH(e.date) DESC
    """)
    List<YearMonthDTO> findAvailableMonths(UUID userId);

    @Query("""
        SELECT e FROM Expense e
        WHERE e.user.id = :userId
            AND e.date BETWEEN :start AND :end
        ORDER BY e.date DESC
    """)
    List<Expense> findByMonthAndUserId(LocalDate start, LocalDate end, UUID userId);

    List<Expense> findByInstallmentGroupIdAndUserId(UUID groupId, UUID userId);

    void deleteByInstallmentGroupIdAndUserId(UUID groupId, UUID userId);

    Optional<Expense> findByIdAndUserId(UUID expenseId, UUID userId);

    boolean existsByCategoryId(UUID categoryId);
}
