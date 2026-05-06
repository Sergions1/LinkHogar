package com.linkhogar.infrastructure.persistence.expense;

import com.linkhogar.domain.expense.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID> {
    List<ExpenseSplit> findByExpenseId(UUID expenseId);

    List<ExpenseSplit> findByDebtorId(UUID debtorId);
}
