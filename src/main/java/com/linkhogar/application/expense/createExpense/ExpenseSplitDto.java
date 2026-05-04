package com.linkhogar.application.expense.createExpense;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseSplitDto(
        UUID debtorId,
        String debtorName,
        BigDecimal amount
) {}
