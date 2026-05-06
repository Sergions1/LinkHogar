package com.linkhogar.application.expense.getHomeBalances;

import java.math.BigDecimal;
import java.util.UUID;

public record UserBalanceDto(
        UUID userId,
        String userName,
        BigDecimal netBalance
) {}