package com.linkhogar.domain.expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {
    Expense save(Expense expense);
    Optional<Expense> findById(UUID id);
    List<Expense> findByHomeIdOrderByCreatedAtDesc(UUID homeId);
    void deleteById(UUID id);
}