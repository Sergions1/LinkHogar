package com.linkhogar.application.expense.GetSplitByExpense;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetSplitByExpenseCommandHandler {
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional(readOnly = true)
    public Result<List<ExpenseSplitResponse>> handle(GetSplitByExpenseCommand query) {

        List<ExpenseSplitResponse> splits = expenseSplitRepository.findByExpenseId(query.expenseId())
                .stream()
                .map(split -> new ExpenseSplitResponse(
                        split.getId(),
                        split.getDebtorId(),
                        split.getDebtorName(),
                        split.getAmountOwed()
                ))
                .collect(Collectors.toList());

        return Result.success(splits);
    }
}
