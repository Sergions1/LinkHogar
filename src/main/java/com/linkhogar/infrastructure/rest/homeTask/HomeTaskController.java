package com.linkhogar.infrastructure.rest.homeTask;

import com.linkhogar.application.chat.getByUser.GetByUserQueryHandle;
import com.linkhogar.application.homeTask.create.CreateHomeTaskCommand;
import com.linkhogar.application.homeTask.create.CreateHomeTaskCommandHandler;
import com.linkhogar.application.homeTask.create.CreateHomeTaskRequest;
import com.linkhogar.application.homeTask.delete.DeleteCommand;
import com.linkhogar.application.homeTask.delete.DeleteCommandHandler;
import com.linkhogar.application.homeTask.getByHome.GetByHomeQuery;
import com.linkhogar.application.homeTask.getByHome.GetByHomeQueryHandler;
import com.linkhogar.application.homeTask.getById.HomeTaskResponse;
import com.linkhogar.application.homeTask.getHomeMembers.GetHomeMembersQuery;
import com.linkhogar.application.homeTask.getHomeMembers.GetHomeMembersQueryHandler;
import com.linkhogar.application.homeTask.getHomeMembers.HomeMemberResponse;
import com.linkhogar.application.homeTask.updateStatus.UpdateStatusCommand;
import com.linkhogar.application.homeTask.updateStatus.UpdateStatusCommandHandler;
import com.linkhogar.application.homeTask.updateStatus.UpdateStatusRequest;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.enums.TaskStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/homeTasks")
@RequiredArgsConstructor
@Tag(name = "Home Tasks")
public class HomeTaskController {

    private final CreateHomeTaskCommandHandler createHomeTaskCommandHandler;
    private final GetByHomeQueryHandler getByHomeQueryHandler;
    private final UpdateStatusCommandHandler updateStatusCommandHandler;
    private final DeleteCommandHandler deleteCommandHandler;
    private final GetHomeMembersQueryHandler getHomeMembersQueryHandler;

    @PostMapping("/create")
    public ResponseEntity<?> createHomeTask(@RequestBody CreateHomeTaskRequest request, Authentication authentication) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID creatorId = UUID.fromString(authentication.getName());

        CreateHomeTaskCommand command = new CreateHomeTaskCommand(
                request.title(),
                request.description(),
                request.homeId(),
                request.assignedUserId(),
                request.assignedUserName(),
                creatorId,
                request.createdByName(),
                request.startDate(),
                request.dueDate()
        );

        Result<UUID> result = createHomeTaskCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().body(new Object() {
                public final UUID taskId = result.getValue();
            });
        } else {
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @GetMapping("/getByHome/{homeId}")
    public ResponseEntity<?> getHomeTasks(@PathVariable UUID homeId, Authentication authentication) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UUID userId = UUID.fromString(authentication.getName());

        Result<List<HomeTaskResponse>> result = getByHomeQueryHandler.handle(new GetByHomeQuery(homeId), userId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        } else {
            System.out.println("ERROR EN GET HOME TASKS: " + result.getError());
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID taskId,
                                          @RequestBody UpdateStatusRequest request,
                                          Authentication authentication){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UUID requesterId = UUID.fromString(authentication.getName());

        TaskStatus status = TaskStatus.valueOf(request.status());

        UpdateStatusCommand command = new UpdateStatusCommand(
                taskId,
                status,
                requesterId
        );

        Result<Void> result = updateStatusCommandHandler.handler(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result.getError());
        }

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteHomeTask(@PathVariable UUID taskId, Authentication authentication){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Result<Void> result = deleteCommandHandler.handle(new DeleteCommand(taskId));

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @GetMapping("/{homeId}/members")
    public ResponseEntity<?> getHomeMembers(
            @PathVariable UUID homeId,
            Authentication authentication) {

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID requesterId = UUID.fromString(authentication.getName());

        GetHomeMembersQuery query = new GetHomeMembersQuery(homeId, requesterId);
        Result<List<HomeMemberResponse>> result = getHomeMembersQueryHandler.handle(query);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        } else {
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

}
