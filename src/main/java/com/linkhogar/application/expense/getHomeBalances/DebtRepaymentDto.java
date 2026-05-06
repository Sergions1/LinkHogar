package com.linkhogar.application.expense.getHomeBalances;

import java.math.BigDecimal;
import java.util.UUID;

public record DebtRepaymentDto(
        UUID debtorId,
        String debtorName,
        UUID creditorId,
        String creditorName,
        BigDecimal amount
) {}