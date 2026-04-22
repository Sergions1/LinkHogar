package com.linkhogar.application.house.getAllReports;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.HouseReport;
import com.linkhogar.domain.house.HouseReportRepository;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.domain.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllReportsQueryHandler {
    private final HouseReportRepository houseReportRepository;
    private final UserRepository userRepository;

    public Result<Page<HouseReport>> handle(GetAllReportsQuery query, Pageable pageable) {
        Optional<User> userOptional = userRepository.userById(query.userId());
        User user;
        if(userOptional.isEmpty()) {
            return Result.failure(UserErrors.NotFound(query.userId()));
        }
        user = userOptional.get();

        if(user.getRole() == Role.Admin || user.getRole() == Role.LinkHogar) {
           Page<HouseReport> reports = houseReportRepository.getAll(pageable);
            return Result.success(reports);
        }else {
            return Result.failure(UserErrors.UNAUTHORIZED);
        }
    }
}
