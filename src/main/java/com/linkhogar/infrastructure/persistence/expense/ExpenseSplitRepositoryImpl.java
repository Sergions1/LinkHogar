package com.linkhogar.infrastructure.persistence.expense;

import com.linkhogar.domain.expense.ExpenseSplit;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExpenseSplitRepositoryImpl implements ExpenseSplitRepository {
    private final JpaExpenseSplitRepository jpaExpenseSplitRepository;

    @Override
    public ExpenseSplit save(ExpenseSplit split) {
        return jpaExpenseSplitRepository.save(split);
    }

    @Override
    public List<ExpenseSplit> saveAll(Iterable<ExpenseSplit> splits) {
        return jpaExpenseSplitRepository.saveAll(splits);
    }

    @Override
    public List<ExpenseSplit> findByExpenseId(UUID expenseId) {
        return jpaExpenseSplitRepository.findByExpenseId(expenseId);
    }

    @Override
    public List<ExpenseSplit> findByDebtorId(UUID debtorId) {
        return jpaExpenseSplitRepository.findByDebtorId(debtorId);
    }

    @Override
    public void deleteByExpenseId(UUID expenseId) {
        jpaExpenseSplitRepository.deleteById(expenseId);
    }

    @Override
    public Optional<ExpenseSplit> findById(UUID spliId) {
        return jpaExpenseSplitRepository.findById(spliId);
    }
}
