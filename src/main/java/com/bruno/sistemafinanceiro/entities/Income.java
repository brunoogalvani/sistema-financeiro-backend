package com.bruno.sistemafinanceiro.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_incomes")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Column(nullable = false)
    private BigDecimal amount;

    @Setter
    @Column(nullable = false)
    private LocalDate startDate;

    @Setter
    @Column(nullable = true)
    private LocalDate endDate;
}
