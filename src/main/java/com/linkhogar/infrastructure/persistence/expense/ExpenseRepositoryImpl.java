package com.linkhogar.infrastructure.persistence.expense;

import com.linkhogar.domain.expense.Expense;
import com.linkhogar.domain.expense.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepository {
    private final JpaExpenseRepository jpaExpenseRepository;

    @Override
    public Expense save(Expense expense) {
        return jpaExpenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> findById(UUID id) {
        return jpaExpenseRepository.findById(id);
    }

    @Override
    public List<Expense> findByHomeIdOrderByCreatedAtDesc(UUID homeId) {
        return jpaExpenseRepository.findByHomeIdOrderByCreatedAtDesc(homeId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaExpenseRepository.deleteById(id);
    }
}
