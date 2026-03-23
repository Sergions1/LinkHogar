package com.linkhogar.infrastructure.rest.admin;

import com.linkhogar.application.admin.DashboardStatsResponse;
import com.linkhogar.application.admin.createUserbyAdmin.CreateUserByAdminCommand;
import com.linkhogar.application.admin.createUserbyAdmin.CreateUserByAdminHandler;
import com.linkhogar.application.admin.createUserbyAdmin.CreateUserByAdminResponse;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.infrastructure.persistence.user.JpaUserRepository;
import com.linkhogar.infrastructure.persistence.house.JpaHouseRepository;
// ... tus imports

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Operaciones relacionadas con la gestión de administracion")
public class AdminController {

    private final JpaUserRepository userRepository;
    private final JpaHouseRepository houseRepository;
    private final CreateUserByAdminHandler createUserByAdminHandler;

    @GetMapping("/stats")
    public DashboardStatsResponse getDashboardStats() {

        long totalUsers = userRepository.count();

        long pendingHouses = houseRepository.countByPublicationStatus(PublicationStatus.PENDING_REVIEW);
        long publishedHouses = houseRepository.countByPublicationStatus(PublicationStatus.PUBLISHED);

        return new DashboardStatsResponse(totalUsers, pendingHouses, publishedHouses);
    }

    @PostMapping("/create-user")
    @Operation(summary = "Crea un usuario desde el panel de administración")
    public ResponseEntity<?> createUserByAdmin(@RequestBody CreateUserByAdminCommand command) {
        try {
            CreateUserByAdminResponse response = createUserByAdminHandler.handle(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}