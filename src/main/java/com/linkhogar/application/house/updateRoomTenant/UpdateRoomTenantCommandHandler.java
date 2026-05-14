package com.linkhogar.application.house.updateRoomTenant;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.room.Room;
import com.linkhogar.domain.room.TenantProfile;
import com.linkhogar.domain.room.enums.RoomStatus;
import com.linkhogar.domain.user.UserErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRoomTenantCommandHandler {
    private final HouseRepository houseRepository;

    @Transactional
    public Result<Void> handle(UpdateRoomTenantCommand command) {
        House house = houseRepository.getById(command.houseId());
        if (house == null) {
            return Result.failure(HouseErrors.NotFound(command.houseId()));
        }

        boolean isOwner = house.getOwner().getId().equals(command.userId());
        boolean isAdmin = command.authorities().stream().anyMatch(a -> a.getAuthority().equals("Admin"));
        if (!isOwner && !isAdmin) {
            return Result.failure(UserErrors.UNAUTHORIZED);
        }

        Room targetRoom = house.getRoomList().stream()
                .filter(r -> r.getId().equals(command.roomId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        targetRoom.setStatus(command.status());

        if (command.status() == RoomStatus.AVAILABLE || command.tenant() == null) {
            targetRoom.setCurrentTenant(null);
        } else {
            TenantProfile tenantProfile = TenantProfile.builder()
                    .gender(command.tenant().gender())
                    .ageRange(command.tenant().ageRange())
                    .occupation(command.tenant().occupation())
                    .description(command.tenant().description())
                    .isSmoker(command.tenant().isSmoker())
                    .hasPets(command.tenant().hasPets())
                    .build();

            targetRoom.setCurrentTenant(tenantProfile);
        }

        houseRepository.save(house);
        return Result.success(null);
    }
}