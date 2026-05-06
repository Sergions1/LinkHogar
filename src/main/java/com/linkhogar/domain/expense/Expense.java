package com.linkhogar.domain.expense;

import com.linkhogar.domain.expense.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expense")
public class Expense {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID homeId;

    @Column(nullable = false)
    private UUID payerId; // El que pagó (y al que le deben el dinero)

    private String payerName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    protected void validate() {
        if (this.category == ExpenseCategory.OTROS) {
            if (this.description == null || this.description.trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción es obligatoria cuando la categoría es OTROS.");
            }
        }
    }
}