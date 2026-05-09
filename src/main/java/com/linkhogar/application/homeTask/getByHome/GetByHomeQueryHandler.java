package com.linkhogar.application.homeTask.getByHome;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.application.homeTask.getById.HomeTaskResponse;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeTaskRepository;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetByHomeQueryHandler {
    private final HomeTaskRepository homeTaskRepository;
    private final UserRepository userRepository;

    public Result<List<HomeTaskResponse>> handle(GetByHomeQuery query, UUID userId) {
        try {

            List<HomeTaskResponse> tasks = homeTaskRepository.findByHomeId(query.homeId())
                    .stream()
                    .map(task -> new HomeTaskResponse(
                            task.getId(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getStatus().name(),
                            task.getAssignedUserId(),
                            task.getAssignedUserName(),
                            task.getCreatedByName(),
                            task.getStartDate(),
                            task.getDueDate(),
                            task.getCompletedAt()
                    ))
                    .collect(Collectors.toList());

            return Result.success(tasks);
        } catch (Exception e) {
            System.out.println("Error al obtener las tareas: " + e.getMessage());
            return Result.failure(Error.failure("500", "No se pudieron obtener las tareas"));
        }
    }
}
