package com.linkhogar.application.expense.deleteExpense;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.Expense;
import com.linkhogar.domain.expense.ExpenseErrors;
import com.linkhogar.domain.expense.ExpenseRepository;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteExpenseCommandHandler {
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional
    public Result<Void> handle(DeleteExpenseCommand command) {
        Optional<Expense> expenseOpt = expenseRepository.findById(command.expenseId());

        if (expenseOpt.isEmpty()) {
            return Result.failure(ExpenseErrors.NotFound(command.expenseId()));
        }

        //Eliminamos los splits asociados
        expenseSplitRepository.deleteByExpenseId(command.expenseId());
        expenseRepository.deleteById(command.expenseId());

        return Result.success(null);
    }

}
