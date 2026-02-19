package com.linkhogar.application.house.getByCity;

import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.HouseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private int size;
    private int rooms;
    private int baths;
    private long price;
    private Address address;
}
