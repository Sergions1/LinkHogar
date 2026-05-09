package com.linkhogar.application.expense.createExpense;

import com.linkhogar.application.notifications.createNotification.CreateNotificationCommand;
import com.linkhogar.application.notifications.createNotification.CreateNotificationCommandHandler;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.Expense;
import com.linkhogar.domain.expense.ExpenseErrors;
import com.linkhogar.domain.expense.ExpenseRepository;
import com.linkhogar.domain.expense.ExpenseSplit;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import com.linkhogar.domain.expense.enums.ExpenseCategory;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.infrastructure.externalServices.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateExpenseCommandHandler {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final CreateNotificationCommandHandler notificationHandler;
    private final SimpMessagingTemplate messagingTemplate;
    private final MailService mailService;
    private final UserRepository userRepository;

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

        messagingTemplate.convertAndSend("/topic/home." + command.homeId() + ".expenses", "REFRESH");

        for (ExpenseSplitDto split : command.splits()) {

            if (split.debtorId().equals(command.payerId())) {
                continue;
            }

            String title = "Nuevo gasto de " + command.payerName();
            String message = "Se ha registrado el gasto '" + command.description() + "'. Te toca pagar " + split.amount() + "€.";

            //Notificación en Base de Datos (Campanita)
            notificationHandler.handle(new CreateNotificationCommand(
                    split.debtorId(),
                    title,
                    message
            ));

            // Notificación en Tiempo Real (WebSocket)
            Map<String, String> wsNotification = new HashMap<>();
            wsNotification.put("title", title);
            wsNotification.put("message", message);
            wsNotification.put("type", "EXPENSE");

            // IMPORTANTE: El canal coincide con /topic/user.{userId} que espera Angular
            messagingTemplate.convertAndSend("/topic/user." + split.debtorId(), wsNotification);

            // D) Correo Electrónico
            userRepository.userById(split.debtorId()).ifPresent(user -> {
                if (user.getMail() != null) {
                    mailService.sendNewExpenseEmail(
                            user.getMail(),
                            command.payerName(),
                            command.description(),
                            split.amount()
                    );
                }
            });
        }

        return Result.success(newExpense.getId());
    }
}