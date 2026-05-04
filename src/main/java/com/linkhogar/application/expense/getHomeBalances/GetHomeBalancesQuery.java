package com.linkhogar.application.expense.getHomeBalances;

import java.util.UUID;

public record GetHomeBalancesQuery(
        UUID homeId
) {}