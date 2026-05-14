package com.linkhogar.application.house.update;

import com.linkhogar.application.house.create.CreateHouseCommand;
import com.linkhogar.application.house.create.CreateHouseResponse;
import com.linkhogar.application.house.create.CreateRoomDto;
import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.house.enums.RentalMode;
import com.linkhogar.domain.room.Room;
import com.linkhogar.domain.room.enums.RoomStatus;
import com.linkhogar.domain.user.UserErrors;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UpdateCommandHandler {
    private final HouseRepository houseRepository;

    @Transactional
    public Result<CreateHouseResponse> handle(UpdateCommand command) {

        House house = houseRepository.getById(command.houseId());

        if (house == null) {
            return Result.failure(HouseErrors.NotFound(command.houseId()));
        }

        boolean isAdminOrStaff = command.authorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "Admin") || Objects.equals(auth.getAuthority(), "LinkHogar"));

        boolean isOwner = house.getOwner().getId().equals(command.userId());

        if (!isAdminOrStaff && !isOwner) {
            return Result.failure(UserErrors.UNAUTHORIZED);
        }

        // 3. Mapeo de datos (Usando tu estructura plana de CreateHouseCommand)
        CreateHouseCommand dto = command.data();

        // Información básica
        house.setTitle(dto.getTitle());
        house.setDescription(dto.getDescription());
        house.setPrice(dto.getPrice());
        house.setHouseType(dto.getHouseType());
        house.setUpdateDate(LocalDateTime.now()); // Marca de tiempo de edición

        // Dimensiones
        house.setSize(dto.getSize());
        house.setRooms(dto.getRooms());
        house.setBaths(dto.getBaths());
        house.setRentalMode(dto.getRentalMode());

        // Dirección
        Address address = house.getAddress();
        if (address == null) {
            address = new Address();
        }

        // Actualizamos los campos dentro del objeto Address
        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setFloor(dto.getFloor());
        address.setDoor(dto.getDoor());
        address.setCity(dto.getCity());
        address.setCp(dto.getCp());
        address.setProvince(dto.getProvince());
        address.setCountry(dto.getCountry());
        // Localización geográfica
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());

        house.setAddress(address);

        // Características (Booleanos)
        house.setLift(dto.getLift());
        house.setFurnished(dto.getFurnished());
        house.setAirConditioned(dto.getAirConditioned());
        house.setTerrace(dto.getTerrace());
        house.setBalcony(dto.getBalcony());
        house.setGarage(dto.getGarage());
        house.setStorage(dto.getStorage());
        house.setPool(dto.getPool());
        house.setCommonAreas(dto.getCommonAreas());
        house.setPetsAllowed(dto.getPetsAllowed());

        if (dto.getRentalMode() == RentalMode.BY_ROOM && dto.getRoomList() != null) {
            List<CreateRoomDto> incomingRooms = dto.getRoomList();

            for (CreateRoomDto dtoRoom : incomingRooms) {
                // Buscamos si la habitación ya existe por su nombre
                Room existingRoom = house.getRoomList().stream()
                        .filter(r -> r.getName().equals(dtoRoom.name()))
                        .findFirst()
                        .orElse(null);

                if (existingRoom != null) {
                    // Actualizamos la existente (así mantenemos su ID y sus fotos previas)
                    existingRoom.setPrice(dtoRoom.price());
                    existingRoom.setDescription(dtoRoom.description());
                    existingRoom.setSize(dtoRoom.size());
                    existingRoom.setHasPrivateBath(dtoRoom.hasPrivateBath());
                    existingRoom.setBedType(dtoRoom.bedType());
                } else {
                    // Creamos una nueva
                    Room newRoom = Room.builder()
                            .name(dtoRoom.name())
                            .description(dto.getDescription())
                            .price(dtoRoom.price())
                            .size(dtoRoom.size())
                            .hasPrivateBath(dtoRoom.hasPrivateBath())
                            .bedType(dtoRoom.bedType())
                            .status(RoomStatus.AVAILABLE)
                            .house(house) // 👈 Relación bidireccional
                            .build();
                    house.getRoomList().add(newRoom);
                }
            }

            // Recalculamos el precio mínimo
            Long minAvailablePrice = house.getRoomList().stream()
                    .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                    .map(Room::getPrice)
                    .min(Long::compareTo)
                    .orElse(0L);
            house.setPrice(minAvailablePrice);
        }

        // 4. Guardar cambios
        houseRepository.save(house);

        Map<String, UUID> roomIds = new HashMap<>();
        if (house.getRoomList() != null) {
            house.getRoomList().forEach(room -> roomIds.put(room.getName(), room.getId()));
        }

        return Result.success(new CreateHouseResponse(house.getId(), roomIds));
    }
}
