package com.linkhogar.application.house.get;

import com.linkhogar.application.Address.AddressResponse;
import com.linkhogar.application.user.getById.UserResponse;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.HouseType;
import com.linkhogar.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HouseResponse {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime publicationDate;
    private LocalDateTime updateDate;
    private HouseType houseType;
    private PublicationStatus publicationStatus;
    private HouseStatus status;

    private int size;
    private int rooms;
    private int baths;

    private boolean lift = false;
    private boolean furnished = false;
    private boolean airConditioned = false;
    private boolean terrace = false;
    private boolean balcony = false;
    private boolean garage = false;
    private boolean storage = false;
    private boolean pool = false;
    private boolean commonAreas = false;
    private boolean petsAllowed = false;

    private long price;
    private AddressResponse address;
    private UserResponse owner;

    public static HouseResponse mapToResponse(House house) {
        if (house == null) return null;

        return HouseResponse.builder()
                .id(house.getId())
                .title(house.getTitle())
                .description(house.getDescription())
                .creationDate(house.getCreationDate())
                .publicationDate(house.getPublicationDate())
                .updateDate(house.getUpdateDate())
                .houseType(house.getHouseType())
                .publicationStatus(house.getPublicationStatus())
                .status(house.getStatus())
                .size(house.getSize())
                .rooms(house.getRooms())
                .baths(house.getBaths())

                // Mapeo de Booleans (Lombok genera isName() o getName())
                .lift(house.isLift())
                .furnished(house.isFurnished())
                .airConditioned(house.isAirConditioned())
                .terrace(house.isTerrace())
                .balcony(house.isBalcony())
                .garage(house.isGarage())
                .storage(house.isStorage())
                .pool(house.isPool())
                .commonAreas(house.isCommonAreas())
                .petsAllowed(house.isPetsAllowed())

                .price(house.getPrice())

                // 👇 Mapeo MANUAL de la Dirección para romper el bucle infinito
                .address(AddressResponse.mapAddress(house.getAddress()))

                // 👇 Mapeo SEGURO del Usuario (solo info pública)
                .owner(UserResponse.mapToResponse(house.getOwner()))

                .build();
    }
}
