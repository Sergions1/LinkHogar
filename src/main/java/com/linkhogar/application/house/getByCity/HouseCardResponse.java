package com.linkhogar.application.house.getByCity;

import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.HouseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    private int size;
    private int rooms;
    private int baths;
    private long price;
    private Address address;

    private List<String> images;



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
                house.getSize(),
                house.getRooms(),
                house.getBaths(),
                house.getPrice(),
                house.getAddress(),
                house.getImages()
        );
    }
}
