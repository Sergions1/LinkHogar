package com.linkhogar.application.house.createReport;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.HouseReport;
import com.linkhogar.domain.house.HouseReportRepository;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateReportCommandHandler {

    private final HouseReportRepository houseReportRepository;
    private final UserRepository userRepository;

    public Result<Void> handle(CreateReportCommand command) {
        User user = userRepository.userById(command.userId()).orElse(null);

        if(user == null) {
            return Result.failure(UserErrors.NotFound(command.userId()));
        }

        try {
            HouseReport report = HouseReport.builder()
                    .houseId(command.houseId())
                    .userId(command.userId())
                    .reason(command.reason())
                    .description(command.description())
                    .createdAt(LocalDateTime.now())
                    .userName(user.getFirstName()+" "+user.getLastName())
                    .build();

            houseReportRepository.save(report);

            return Result.success(null);

        } catch (Exception e) {
            return Result.failure(Error.failure("401","No se ha podido crear el report" ));
        }
    }
}