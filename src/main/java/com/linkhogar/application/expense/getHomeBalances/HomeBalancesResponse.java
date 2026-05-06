package com.linkhogar.application.expense.getHomeBalances;

import java.util.List;

public record HomeBalancesResponse(
        List<UserBalanceDto> balances,
        List<DebtRepaymentDto> repayments
) {}