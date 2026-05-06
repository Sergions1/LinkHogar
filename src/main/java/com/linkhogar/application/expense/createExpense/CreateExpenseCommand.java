package com.linkhogar.application.expense.createExpense;

import com.linkhogar.domain.expense.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateExpenseCommand(
        UUID homeId,
        UUID payerId,
        String payerName,
        BigDecimal amount,
        String description,
        ExpenseCategory category,
        List<ExpenseSplitDto> splits
) {
}
