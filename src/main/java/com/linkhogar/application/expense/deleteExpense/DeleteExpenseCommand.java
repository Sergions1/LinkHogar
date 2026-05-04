package com.linkhogar.application.expense.deleteExpense;

import java.util.UUID;

public record DeleteExpenseCommand (
        UUID expenseId,
        UUID expernserId
){
}
