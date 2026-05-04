package com.linkhogar.domain.expense;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expense_split")
public class ExpenseSplit {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID expenseId;

    @Column(nullable = false)
    private UUID debtorId; // El usuario que tiene que pagar esta parte

    private String debtorName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountOwed; // La cantidad exacta que le toca pagar
}