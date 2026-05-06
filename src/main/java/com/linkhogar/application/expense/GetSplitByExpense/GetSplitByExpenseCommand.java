package com.linkhogar.application.expense.GetSplitByExpense;

import java.math.BigDecimal;
import java.util.UUID;

public record GetSplitByExpenseCommand(
        UUID expenseId
) {}