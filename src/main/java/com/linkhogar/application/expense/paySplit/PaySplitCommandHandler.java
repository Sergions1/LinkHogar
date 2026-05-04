package com.linkhogar.application.expense.paySplit;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.expense.ExpenseErrors;
import com.linkhogar.domain.expense.ExpenseSplit;
import com.linkhogar.domain.expense.ExpenseSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaySplitCommandHandler {
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional
    public Result<Void> handle(PaySplitCommand command) {
        Optional<ExpenseSplit> splitOpt = expenseSplitRepository.findById(command.splitId());

        if (splitOpt.isEmpty()) {
            return Result.failure(ExpenseErrors.SPLIT_NOT_FOUND);
        }

        ExpenseSplit split = splitOpt.get();
        split.setPaid(true); // Lo marcamos como pagado

        expenseSplitRepository.save(split);

        return Result.success(null);
    }
}
