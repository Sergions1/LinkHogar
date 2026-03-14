package com.linkhogar.application.house.create;

import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.address.AddressRepository;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateHouseCommandHandler {
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public void handle(CreateHouseCommand command, String userId) {
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

        houseRepository.save(house);
    }
}
