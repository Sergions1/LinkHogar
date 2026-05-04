package com.linkhogar.application.expense.getHomeBalances;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.Expense;
import com.linkhogar.domain.expense.ExpenseRepository;
import com.linkhogar.domain.expense.ExpenseSplit;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetHomeBalancesQueryHandler {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional(readOnly = true)
    public Result<HomeBalancesResponse> handle(GetHomeBalancesQuery query) {

        List<Expense> expenses = expenseRepository.findByHomeIdOrderByCreatedAtDesc(query.homeId());
        Map<UUID, BigDecimal> userBalances = new HashMap<>();
        Map<UUID, String> userNames = new HashMap<>();

        // 1. CALCULAR BALANCES NETOS
        for (Expense expense : expenses) {
            // El que paga asume el gasto completo a su favor
            userBalances.merge(expense.getPayerId(), expense.getAmount(), BigDecimal::add);
            userNames.put(expense.getPayerId(), expense.getPayerName());

            List<ExpenseSplit> splits = expenseSplitRepository.findByExpenseId(expense.getId());
            for (ExpenseSplit split : splits) {
                // Restamos la parte que le toca a cada deudor
                userBalances.merge(split.getDebtorId(), split.getAmountOwed().negate(), BigDecimal::add);
                userNames.put(split.getDebtorId(), split.getDebtorName());

                // Si el split ya está pagado, revertimos esa deuda parcial
                // (El deudor recupera su balance, y el pagador ya no cuenta con ese dinero a su favor)
                if (split.isPaid()) {
                    userBalances.merge(split.getDebtorId(), split.getAmountOwed(), BigDecimal::add);
                    userBalances.merge(expense.getPayerId(), split.getAmountOwed().negate(), BigDecimal::add);
                }
            }
        }

        // Formatear la lista de balances generales para devolverla al frontend
        List<UserBalanceDto> balancesDto = userBalances.entrySet().stream()
                .map(e -> new UserBalanceDto(
                        e.getKey(),
                        userNames.get(e.getKey()), // Añadimos el nombre al DTO
                        e.getValue().setScale(2, RoundingMode.HALF_UP)
                ))
                .collect(Collectors.toList());

        // 2. SEPARAR ACREEDORES (balance > 0) Y DEUDORES (balance < 0)
        List<MutableBalance> creditors = new ArrayList<>();
        List<MutableBalance> debtors = new ArrayList<>();

        userBalances.forEach((userId, balance) -> {
            String userName = userNames.get(userId);
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new MutableBalance(userId, userName, balance));
            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                // Guardamos la deuda en positivo para facilitar el cálculo
                debtors.add(new MutableBalance(userId, userName, balance.negate()));
            }
        });

        // 3. CRUZAR SALDOS (El algoritmo de simplificación de deudas)
        List<DebtRepaymentDto> repayments = new ArrayList<>();
        int i = 0; // Índice de deudores
        int j = 0; // Índice de acreedores

        while (i < debtors.size() && j < creditors.size()) {
            MutableBalance debtor = debtors.get(i);
            MutableBalance creditor = creditors.get(j);

            // La cantidad a saldar es el mínimo entre lo que el deudor debe y lo que el acreedor espera
            BigDecimal settleAmount = debtor.amount.min(creditor.amount);

            // Solo añadimos la transacción si la cantidad es mayor que 0
            if (settleAmount.compareTo(BigDecimal.ZERO) > 0) {
                repayments.add(new DebtRepaymentDto(
                        debtor.userId,
                        debtor.userName,
                        creditor.userId,
                        creditor.userName,
                        settleAmount.setScale(2, RoundingMode.HALF_UP)
                ));
            }

            // Actualizamos las cantidades restantes
            debtor.amount = debtor.amount.subtract(settleAmount);
            creditor.amount = creditor.amount.subtract(settleAmount);

            // Si ya no debe nada, pasamos al siguiente deudor
            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0) {
                i++;
            }
            // Si ya cobró, pasamos al siguiente acreedor
            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0) {
                j++;
            }
        }

        return Result.success(new HomeBalancesResponse(balancesDto, repayments));
    }

    // Clase auxiliar interna para poder modificar las cantidades durante el bucle while
    @Data
    @AllArgsConstructor
    private static class MutableBalance {
        private UUID userId;
        private String userName;
        private BigDecimal amount;
    }
}