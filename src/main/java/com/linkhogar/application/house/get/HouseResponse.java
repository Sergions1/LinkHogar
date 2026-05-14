package com.linkhogar.application.house.get;

import com.linkhogar.application.Address.AddressResponse;
import com.linkhogar.application.house.getById.RoomResponse;
import com.linkhogar.application.house.getById.TenantProfileResponse;
import com.linkhogar.application.user.getById.UserResponse;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.HouseType;
import com.linkhogar.domain.house.enums.RentalMode;
import com.linkhogar.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


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

    private List<String> images;

    private RentalMode rentalMode;
    private List<RoomResponse> roomList;

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
                .lift(house.getLift())
                .furnished(house.getFurnished())
                .airConditioned(house.getAirConditioned())
                .terrace(house.getTerrace())
                .balcony(house.getBalcony())
                .garage(house.getGarage())
                .storage(house.getStorage())
                .pool(house.getPool())
                .commonAreas(house.getCommonAreas())
                .petsAllowed(house.getPetsAllowed())

                .price(house.getPrice())
                .images(house.getImages())
                // 👇 Mapeo MANUAL de la Dirección para romper el bucle infinito
                .address(AddressResponse.mapAddress(house.getAddress()))

                // 👇 Mapeo SEGURO del Usuario (solo info pública)
                .owner(UserResponse.mapToResponse(house.getOwner()))

                .rentalMode(house.getRentalMode())

                .roomList(house.getRoomList() != null ? house.getRoomList().stream()
                        .map(room -> new RoomResponse(
                                room.getId().toString(),
                                room.getName(),
                                room.getDescription(),
                                room.getPrice(),
                                room.getSize(),
                                room.isHasPrivateBath(),
                                room.getBedType(),
                                room.getStatus(),
                                room.getCurrentTenant() != null ? new TenantProfileResponse(
                                        room.getCurrentTenant().getGender(),
                                        room.getCurrentTenant().getAgeRange(),
                                        room.getCurrentTenant().getOccupation(),
                                        room.getCurrentTenant().getDescription(),
                                        room.getCurrentTenant().getIsSmoker(),
                                        room.getCurrentTenant().getHasPets()
                                ) : null,
                                room.getPhotoUrls()
                        )).collect(Collectors.toList()) : List.of())


                .build();
    }
}
