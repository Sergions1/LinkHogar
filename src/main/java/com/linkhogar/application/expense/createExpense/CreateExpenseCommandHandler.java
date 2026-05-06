package com.linkhogar.application.expense.createExpense;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.Expense;
import com.linkhogar.domain.expense.ExpenseErrors;
import com.linkhogar.domain.expense.ExpenseRepository;
import com.linkhogar.domain.expense.ExpenseSplit;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import com.linkhogar.domain.expense.enums.ExpenseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateExpenseCommandHandler {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional
    public Result<UUID> handle(CreateExpenseCommand command) {

        // 1. Validación de negocio: Descripción obligatoria para "OTROS"
        if (command.category() == ExpenseCategory.OTROS &&
                (command.description() == null || command.description().trim().isEmpty())) {
            return Result.failure(ExpenseErrors.MISSING_DESCRIPTION);
        }

        // 2. Validación contable: La suma de los splits debe ser igual al monto total
        BigDecimal splitsSum = command.splits().stream()
                .map(ExpenseSplitDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Usamos compareTo(0) para comparar BigDecimals de forma segura
        if (splitsSum.compareTo(command.amount()) != 0) {
            return Result.failure(ExpenseErrors.INVALID_SPLIT_AMOUNT);
        }

        // 3. Crear y guardar el Gasto principal
        Expense newExpense = Expense.builder()
                .id(UUID.randomUUID())
                .homeId(command.homeId())
                .payerId(command.payerId())
                .payerName(command.payerName())
                .amount(command.amount())
                .description(command.description())
                .category(command.category())
                .createdAt(LocalDateTime.now())
                .build();

        expenseRepository.save(newExpense);

        // 4. Crear y guardar las divisiones (Splits)
        List<ExpenseSplit> splitsToSave = command.splits().stream()
                .map(dto -> ExpenseSplit.builder()
                        .id(UUID.randomUUID())
                        .expenseId(newExpense.getId())
                        .debtorId(dto.debtorId())
                        .debtorName(dto.debtorName())
                        .amountOwed(dto.amount())
                        .isPaid(dto.debtorId().equals(command.payerId()))
                        .build())
                .collect(Collectors.toList());

        expenseSplitRepository.saveAll(splitsToSave);

        return Result.success(newExpense.getId());
    }
}