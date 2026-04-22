package com.linkhogar.application.house.deleteReport;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseReportRepository;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteReportCommandHandler {
    private final HouseReportRepository houseReportRepository;

    public Result<Void> handle(DeleteReportCommand command){
        if(houseReportRepository.findById(command.reportId())==null){
            return Result.failure(Error.notFound("401", "Report no encontrado"));
        }

        houseReportRepository.deleteById(command.reportId());

        return Result.success(null);
    }
}
