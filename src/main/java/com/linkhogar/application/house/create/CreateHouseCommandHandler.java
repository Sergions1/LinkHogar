package com.linkhogar.application.house.create;

import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.address.AddressRepository;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.RentalMode;
import com.linkhogar.domain.room.Room;
import com.linkhogar.domain.room.enums.RoomStatus;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateHouseCommandHandler {
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UUID handle(CreateHouseCommand command, String userId) {
        User owner = userRepository.userById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException(("El usuario del token no existe")));

        Address address = Address.builder()
                .id(UUID.randomUUID())
                .street(command.getStreet())
                .number(command.getNumber())
                .floor(command.getFloor())
                .door(command.getDoor())
                .city(command.getCity())
                .cp(command.getCp())
                .province(command.getProvince())
                .country(command.getCountry())
                .latitude(command.getLatitude())
                .longitude(command.getLongitude())
                .build();

        addressRepository.save(address);

        House house = House.builder()
                .id(UUID.randomUUID())
                .title(command.getTitle())
                .description(command.getDescription())
                .creationDate(LocalDateTime.now())
                .publicationDate(LocalDateTime.now())
                .updateDate(null)
                .status(HouseStatus.Disponible)
                .houseType(command.getHouseType())
                .publicationStatus(command.getPublicationStatus())
                .size(command.getSize())
                .rooms(command.getRooms())
                .baths(command.getBaths())
                .price(command.getPrice())
                .address(address)
                .owner(owner)
                .build();

        house.setLift(Boolean.TRUE.equals(command.getLift()));
        house.setFurnished(Boolean.TRUE.equals(command.getFurnished()));
        house.setAirConditioned(Boolean.TRUE.equals(command.getAirConditioned()));
        house.setTerrace(Boolean.TRUE.equals(command.getTerrace()));
        house.setBalcony(Boolean.TRUE.equals(command.getBalcony()));
        house.setGarage(Boolean.TRUE.equals(command.getGarage()));
        house.setStorage(Boolean.TRUE.equals(command.getStorage()));
        house.setPool(Boolean.TRUE.equals(command.getPool()));
        house.setCommonAreas(Boolean.TRUE.equals(command.getCommonAreas()));
        house.setPetsAllowed(Boolean.TRUE.equals(command.getPetsAllowed()));

        //Creación de habitaciones
        if (command.getRentalMode() == RentalMode.BY_ROOM && command.getRoomDetails() != null) {
            if (command.getRoomDetails() == null || command.getRoomDetails().size() != command.getRooms()) {
                throw new IllegalArgumentException("Incongruencia de datos: La casa indica tener "
                        + command.getRooms() + " habitaciones, pero se han enviado detalles de "
                        + (command.getRoomDetails() == null ? 0 : command.getRoomDetails().size()) + ".");
            }

            //Mapeamos los DTOs a entidades de Dominio
            List<Room> domainRooms = command.getRoomDetails().stream()
                    .map(this::mapToDomainRoom)
                    .toList();

            house.setRoomList(domainRooms);

            // Buscamos el precio más barato que esté disponible
            Long minAvailablePrice = domainRooms.stream()
                    .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                    .map(Room::getPrice)
                    .min(Long::compareTo)
                    .orElse(0L); // Si todas están ocupadas, se queda en 0

            // Sobrescribimos el precio global con el "Desde X€"
            house.setPrice(minAvailablePrice);
        }

        houseRepository.save(house);

        return house.getId();
    }

    private Room mapToDomainRoom(CreateRoomDto dto) {
        return Room.builder()
                .id(UUID.randomUUID())
                .name(dto.name())
                .price(dto.price())
                .size(dto.size())
                .hasPrivateBath(dto.hasPrivateBath())
                .bedType(dto.bedType())
                .status(dto.status() != null ? dto.status() : RoomStatus.AVAILABLE) //Por defecto estará Disponible
                .currentTenant(null) //Sin inquilino en la creación
                .build();
    }
}
