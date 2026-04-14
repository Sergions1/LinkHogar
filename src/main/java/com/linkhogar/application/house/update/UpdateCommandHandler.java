package com.linkhogar.application.house.update;

import com.linkhogar.application.house.create.CreateHouseCommand;
import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.user.UserErrors;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UpdateCommandHandler {
    private final HouseRepository houseRepository;

    @Transactional
    public Result<Void> handle(UpdateCommand command) {

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



        // 4. Guardar cambios
        houseRepository.save(house);

        return Result.success(null);
    }
}
