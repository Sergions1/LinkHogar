package com.linkhogar.application.expense.getExpenseByHome;

import com.linkhogar.application.expense.GetSplitByExpense.ExpenseSplitResponse;
import com.linkhogar.application.expense.getExpenseByHome.ExpenseResponse;
import com.linkhogar.application.expense.getExpenseByHome.GetExpenseByHomeQuery;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.ExpenseRepository;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetExpenseByHomeQueryHandler {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional(readOnly = true)
    public Result<List<ExpenseResponse>> handle(GetExpenseByHomeQuery query) {

        List<ExpenseResponse> expenses = expenseRepository.findByHomeIdOrderByCreatedAtDesc(query.homeId())
                .stream()
                .map(expense -> {
                    List<ExpenseSplitResponse> splits = expenseSplitRepository.findByExpenseId(expense.getId())
                            .stream()
                            .map(split -> new ExpenseSplitResponse(
                                    split.getId(),
                                    split.getDebtorId(),
                                    split.getDebtorName(),
                                    split.getAmountOwed(),
                                    split.isPaid()
                            ))
                            .collect(Collectors.toList());

                    return new ExpenseResponse(
                            expense.getId(),
                            expense.getPayerId(),
                            expense.getPayerName(),
                            expense.getAmount(),
                            expense.getDescription(),
                            expense.getCategory(),
                            expense.getCreatedAt(),
                            splits
                    );
                })
                .collect(Collectors.toList());

        return Result.success(expenses);
    }
}