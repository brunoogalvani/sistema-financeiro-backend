package com.bruno.sistemafinanceiro.repositories;

import com.bruno.sistemafinanceiro.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncomeRepository extends JpaRepository<Income, UUID> {

    @Query("""
        SELECT i FROM Income i
        WHERE i.user.id = :userId
        AND i.startDate <= :date
        AND (i.endDate IS NULL OR i.endDate >= :date)
    """)
    Optional<Income> findIncomeByDateAndUserId(LocalDate date, UUID userId);

    List<Income> findAllByUserIdOrderByStartDateDesc(UUID userId);
}
