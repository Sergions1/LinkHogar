package com.linkhogar.infrastructure.rest.expense;

import com.linkhogar.application.expense.GetSplitByExpense.ExpenseSplitResponse;
import com.linkhogar.application.expense.GetSplitByExpense.GetSplitByExpenseCommand;
import com.linkhogar.application.expense.createExpense.CreateExpenseCommand;
import com.linkhogar.application.expense.deleteExpense.DeleteExpenseCommand;
import com.linkhogar.application.expense.getByHome.GetExpenseByHomeQueryHandler;
import com.linkhogar.application.expense.GetSplitByExpense.GetSplitByExpenseCommandHandler;
import com.linkhogar.application.expense.createExpense.CreateExpenseCommandHandler;
import com.linkhogar.application.expense.deleteExpense.DeleteExpenseCommandHandler;
import com.linkhogar.application.expense.getExpenseByHome.ExpenseResponse;
import com.linkhogar.application.expense.getExpenseByHome.GetExpenseByHomeQuery;
import com.linkhogar.application.expense.getHomeBalances.GetHomeBalancesQuery;
import com.linkhogar.application.expense.getHomeBalances.GetHomeBalancesQueryHandler;
import com.linkhogar.application.expense.getHomeBalances.HomeBalancesResponse;
import com.linkhogar.application.expense.paySplit.PaySplitCommand;
import com.linkhogar.application.expense.paySplit.PaySplitCommandHandler;
import com.linkhogar.domain.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Gestor de gastos compartidos del hogar")
public class ExpenseController {
    private final CreateExpenseCommandHandler createExpenseCommandHandler;
    private final GetExpenseByHomeQueryHandler getHomeExpensesQueryHandler;
    private final GetSplitByExpenseCommandHandler getExpenseSplitsQueryHandler;
    private final DeleteExpenseCommandHandler deleteExpenseCommandHandler;
    private final PaySplitCommandHandler paySplitCommandHandler;
    private final GetHomeBalancesQueryHandler getHomeBalancesQueryHandler;

    @PostMapping
    @Operation(summary = "Registrar un nuevo gasto y sus divisiones")
    public ResponseEntity<?> createExpense(@RequestBody CreateExpenseCommand command) {
        Result<UUID> result = createExpenseCommandHandler.handle(command);

        if (result.isSuccess()) {
            // Devolvemos el JSON { "id": "uuid..." } exacto que espera Angular
            return ResponseEntity.ok(Map.of("id", result.getValue()));
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }


    }

    @GetMapping("/home/{homeId}")
    @Operation(summary = "Obtener el historial de gastos de una casa")
    public ResponseEntity<?> getHomeExpenses(@PathVariable UUID homeId) {
        GetExpenseByHomeQuery query = new GetExpenseByHomeQuery(homeId);
        Result<List<ExpenseResponse>> result = getHomeExpensesQueryHandler.handle(query);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @GetMapping("/{expenseId}/splits")
    @Operation(summary = "Obtener cómo se divide un gasto específico")
    public ResponseEntity<?> getExpenseSplits(@PathVariable UUID expenseId) {
        GetSplitByExpenseCommand query = new GetSplitByExpenseCommand(expenseId);
        Result<List<ExpenseSplitResponse>> result = getExpenseSplitsQueryHandler.handle(query);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Eliminar un gasto y sus divisiones")
    public ResponseEntity<?> deleteExpense(@PathVariable UUID expenseId) {
        // Si más adelante usas el ID del usuario del token para validar permisos, lo inyectas aquí.
        DeleteExpenseCommand command = new DeleteExpenseCommand(expenseId, null);
        Result<Void> result = deleteExpenseCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @PatchMapping("/splits/{splitId}/pay")
    @Operation(summary = "Marcar una división de gasto como pagada")
    public ResponseEntity<?> markSplitAsPaid(@PathVariable UUID splitId) {
        PaySplitCommand command = new PaySplitCommand(splitId);
        Result<Void> result = paySplitCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else{
            return ResponseEntity.badRequest().body(result.getError());
        }


    }

    @GetMapping("/home/{homeId}/balances")
    @Operation(summary = "Obtener el balance neto y las deudas cruzadas de la casa")
    public ResponseEntity<?> getHomeBalances(@PathVariable UUID homeId) {
        GetHomeBalancesQuery query = new GetHomeBalancesQuery(homeId);
        Result<HomeBalancesResponse> result = getHomeBalancesQueryHandler.handle(query);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }

    }

}
