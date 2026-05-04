package com.linkhogar.domain.expense;

import java.util.List;
import java.util.UUID;

public interface ExpenseSplitRepository {
    ExpenseSplit save(ExpenseSplit split);
    List<ExpenseSplit> saveAll(Iterable<ExpenseSplit> splits);
    List<ExpenseSplit> findByExpenseId(UUID expenseId);
    List<ExpenseSplit> findByDebtorId(UUID debtorId);
    void deleteByExpenseId(UUID expenseId);
}