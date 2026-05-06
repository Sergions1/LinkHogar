package com.linkhogar.domain.expense;

import lombok.NoArgsConstructor;

import java.util.UUID;
import com.linkhogar.domain.common.result.Error;

@NoArgsConstructor
public class ExpenseErrors {

    public static Error NotFound(UUID expenseId) {
        return Error.notFound(
                "Expenses.NotFound",
                "El gasto con el Id = '" + expenseId + "' no fue encontrado"
        );
    }

    public static final Error UNAUTHORIZED_ACCESS = Error.unauthorized(
            "Expenses.UnauthorizedAccess",
            "Acceso denegado: No perteneces a este hogar o no tienes permisos sobre este gasto"
    );

    public static final Error MISSING_DESCRIPTION = Error.conflict(
            "Expenses.MissingDescription",
            "La descripción es obligatoria cuando la categoría es OTROS"
    );

    public static final Error INVALID_SPLIT_AMOUNT = Error.conflict(
            "Expenses.InvalidSplitAmount",
            "La suma de las divisiones de los participantes no coincide con el total del gasto"
    );

    public static final Error CREATION_FAILED = Error.failure(
            "Expenses.CreationFailed",
            "No se pudo registrar el gasto"
    );

    public static final Error DELETION_FAILED = Error.failure(
            "Expenses.DeletionFailed",
            "No se pudo eliminar el gasto"
    );

    public static final Error SPLIT_NOT_FOUND = Error.failure(
            "Expenses.SplitNotFound",
            "No se pudo eliminar el gasto"
    );
}
