package com.linkhogar.application.house.create;

import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.HouseType;
import com.linkhogar.domain.house.enums.RentalMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateHouseCommand {
    private String title;
    private String description;
    private LocalDateTime publicationDate;
    private LocalDateTime updateDate;
    private HouseType houseType;
    private PublicationStatus publicationStatus;
    private HouseStatus status;

    private Integer size;
    private Integer rooms;
    private Integer baths;
    private Long price;

    private String street;
    private int number;
    private String floor;
    private String door;
    private String city;
    private int cp;
    private String province;
    private String country;

    private Boolean lift = false;
    private Boolean furnished= false;
    private Boolean airConditioned= false;
    private Boolean terrace= false;
    private Boolean balcony= false;
    private Boolean garage= false;
    private Boolean storage= false;
    private Boolean pool= false;
    private Boolean commonAreas= false;
    private Boolean petsAllowed= false;

    private Double latitude;
    private Double longitude;

    private RentalMode rentalMode;
    private List<CreateRoomDto> roomList;
}
