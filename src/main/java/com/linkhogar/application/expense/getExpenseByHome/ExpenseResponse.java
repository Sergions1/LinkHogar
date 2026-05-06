package com.linkhogar.application.expense.getExpenseByHome;

import com.linkhogar.application.expense.GetSplitByExpense.ExpenseSplitResponse;
import com.linkhogar.domain.expense.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID payerId,
        String payerName,
        BigDecimal amount,
        String description,
        ExpenseCategory category,
        LocalDateTime createdAt,
        List<ExpenseSplitResponse> splits
) {}
