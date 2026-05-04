package com.linkhogar.application.expense.getByHome;

import com.linkhogar.application.expense.getExpenseByHome.ExpenseResponse;
import com.linkhogar.application.expense.getExpenseByHome.GetExpenseByHomeQuery;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetExpenseByHomeQueryHandler {

    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public Result<List<ExpenseResponse>> handle(GetExpenseByHomeQuery query) {

        List<ExpenseResponse> expenses = expenseRepository.findByHomeIdOrderByCreatedAtDesc(query.homeId())
                .stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getPayerId(),
                        expense.getPayerName(),
                        expense.getAmount(),
                        expense.getDescription(),
                        expense.getCategory(),
                        expense.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return Result.success(expenses);
    }
}