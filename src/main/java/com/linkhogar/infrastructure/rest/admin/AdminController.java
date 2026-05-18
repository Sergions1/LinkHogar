package com.linkhogar.infrastructure.rest.admin;

import com.linkhogar.application.admin.DashboardStatsResponse;
import com.linkhogar.application.admin.createUserbyAdmin.CreateUserByAdminCommand;
import com.linkhogar.application.admin.createUserbyAdmin.CreateUserByAdminHandler;
import com.linkhogar.application.admin.createUserbyAdmin.CreateUserByAdminResponse;
import com.linkhogar.application.house.get.HouseResponse;
import com.linkhogar.application.house.getPendingHouses.GetPendingHousesQuery;
import com.linkhogar.application.house.getPendingHouses.GetPendingHousesQueryHandler;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.HouseReportRepository;
import com.linkhogar.domain.house.enums.ReportStatus;
import com.linkhogar.infrastructure.persistence.house.JpaHouseReportRepository;
import com.linkhogar.infrastructure.persistence.user.JpaUserRepository;
import com.linkhogar.infrastructure.persistence.house.JpaHouseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Operaciones relacionadas con la gestión de administracion")
public class AdminController {

    private final JpaUserRepository userRepository;
    private final JpaHouseRepository houseRepository;
    private final HouseReportRepository houseReportRepository;
    private final CreateUserByAdminHandler createUserByAdminHandler;
    private final GetPendingHousesQueryHandler getPendingHousesQueryHandler;


    @GetMapping("/stats")
    public DashboardStatsResponse getDashboardStats() {

        long totalUsers = userRepository.count();

        long pendingHouses = houseReportRepository.countByPendant();
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

    @GetMapping("/houses/pending")
    public ResponseEntity<Page<HouseResponse>> getPendingHouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try{
            GetPendingHousesQuery query = new GetPendingHousesQuery(page, size);
            return ResponseEntity.ok(getPendingHousesQueryHandler.handle(query));
        }catch (RuntimeException e){
            return ResponseEntity.noContent().build();
        }

    }


}