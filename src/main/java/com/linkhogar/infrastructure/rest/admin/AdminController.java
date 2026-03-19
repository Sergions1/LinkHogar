package com.linkhogar.infrastructure.rest.admin;

import com.linkhogar.application.admin.DashboardStatsResponse;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.infrastructure.persistence.user.JpaUserRepository;
import com.linkhogar.infrastructure.persistence.house.JpaHouseRepository;
// ... tus imports

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Operaciones relacionadas con la gestión de administracion")
public class AdminController {

    private final JpaUserRepository userRepository;
    private final JpaHouseRepository houseRepository;

    public AdminController(JpaUserRepository userRepository, JpaHouseRepository houseRepository) {
        this.userRepository = userRepository;
        this.houseRepository = houseRepository;
    }

    @GetMapping("/stats")
    public DashboardStatsResponse getDashboardStats() {

        long totalUsers = userRepository.count();

        long pendingHouses = houseRepository.countByPublicationStatus(PublicationStatus.PENDING_REVIEW);
        long publishedHouses = houseRepository.countByPublicationStatus(PublicationStatus.PUBLISHED);

        return new DashboardStatsResponse(totalUsers, pendingHouses, publishedHouses);
    }
}