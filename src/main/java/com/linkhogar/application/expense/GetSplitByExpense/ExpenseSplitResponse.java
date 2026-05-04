package com.linkhogar.application.expense.GetSplitByExpense;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseSplitResponse(
        UUID id,
        UUID debtorId,
        String debtorName,
        BigDecimal amountOwed
) {}
