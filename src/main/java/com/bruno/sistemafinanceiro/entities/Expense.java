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
@Table(name = "tb_expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    private String name;

    @Setter
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Setter
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Setter
    private LocalDate date;

    @Setter
    @Column(nullable = true)
    private UUID installmentGroupId;

    @Setter
    @Column(nullable = true)
    private Integer installmentNumber;

    @Setter
    @Column(nullable = true)
    private Integer totalInstallments;
}
