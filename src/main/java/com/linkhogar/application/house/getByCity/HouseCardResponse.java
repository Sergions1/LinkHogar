package com.linkhogar.application.house.getByCity;

import com.linkhogar.application.house.getById.RoomResponse;
import com.linkhogar.application.house.getById.TenantProfileResponse;
import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.HouseType;
import com.linkhogar.domain.house.enums.RentalMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseCardResponse
{
    private String id;
    private String title;
    private String description;
    private LocalDateTime publicationDate;
    private LocalDateTime updateDate;
    private HouseType houseType;
    private HouseStatus status;
    private PublicationStatus publicationStatus;
    private RentalMode rentalMode;

    private int size;
    private int rooms;
    private int baths;
    private long price;
    private Address address;

    private List<String> images;

    private List<RoomResponse> roomList;



    /**
     * @summary Convierte una entidad {@link House} a su DTO de respuesta {@link HouseCardResponse}.
     * @param house La entidad del inmueble a convertir.
     * @return El objeto DTO con los datos para la tarjeta de inmueble.
     */
    public static HouseCardResponse toHouseCardResponse(House house) {
        return new HouseCardResponse(
                house.getId().toString(),
                house.getTitle(),
                house.getDescription(),
                house.getPublicationDate(),
                house.getUpdateDate(),
                house.getHouseType(),
                house.getStatus(),
                house.getPublicationStatus(),
                house.getRentalMode(),
                house.getSize(),
                house.getRooms(),
                house.getBaths(),
                house.getPrice(),
                house.getAddress(),
                house.getImages(),
                house.getRoomList() != null ? house.getRoomList().stream()
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
                        )).collect(Collectors.toList()) : List.of()
        );
    }
}
